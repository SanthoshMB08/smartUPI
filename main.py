from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy.orm import Session
from passlib.hash import bcrypt
from database import SessionLocal, engine
from models import Base, User, Transaction
from pydantic import BaseModel

from datetime import datetime

Base.metadata.create_all(bind=engine)
app = FastAPI()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# Schemas
class BalanceRequest(BaseModel):
    upi_id: str
    pin: str

class UserRegister(BaseModel):
    mobile: str
    upi_id: str
    password: str
    upi_pin: str  # hashed UPI PIN

class UserLogin(BaseModel):
    mobile: str
    password: str

class PaymentRequest(BaseModel):
    sender_upi: str
    receiver_upi: str
    amount: float
    pin: str  # UPI PIN

# Register User
@app.post("/register")
def register(user: UserRegister, db: Session = Depends(get_db)):
    if db.query(User).filter((User.mobile == user.mobile) | (User.upi_id == user.upi_id)).first():
        raise HTTPException(status_code=400, detail="User already exists.")
    db_user = User(
        mobile=user.mobile,
        upi_id=user.upi_id,
        password=bcrypt.hash(user.password),
         upi_pin=bcrypt.hash(user.upi_pin),
        balance=1000  # Default balance
    )
    db.add(db_user)
    db.commit()
    return {"success": True,"message": "User registered"}

# Login User
@app.post("/login")
def login(user: UserLogin, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.mobile == user.mobile).first()
    if not db_user or not bcrypt.verify(user.password, db_user.password):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    return{
        "message": "Login successful",
        "user": {
            "mobile": db_user.mobile,
            "upi_id": db_user.upi_id,
            "balance": db_user.balance
        }}
@app.post("/balance")
def get_balance(req: BalanceRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.upi_id == req.upi_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    if not bcrypt.verify(req.pin, user.upi_pin):
        raise HTTPException(status_code=401, detail="Invalid PIN")
    
    return {
        "upi_id": user.upi_id,
        "balance": user.balance
    }
# Transaction History
@app.get("/transactions")
def get_transactions(upi_id: str, db: Session = Depends(get_db)):
    txns = db.query(Transaction).filter(
        (Transaction.from_upi == upi_id) | (Transaction.to_upi == upi_id)
    ).order_by(Transaction.timestamp.desc()).all()

    return [
        {
            "from_upi": txn.from_upi,
            "to_upi": txn.to_upi,
            "amount": txn.amount,
            "status": txn.status,
            "timestamp": txn.timestamp
        }
        for txn in txns
    ]


# Pay / Sync Offline Payment
@app.post("/pay")
def pay(req: PaymentRequest, db: Session = Depends(get_db)):
    sender = db.query(User).filter(User.upi_id == req.sender_upi).first()
    receiver = db.query(User).filter(User.upi_id == req.receiver_upi).first()

    if not sender :
        raise HTTPException(status_code=404, detail="Invalid UPI ID for sender")
        print("sender")
    if not receiver:
        raise HTTPException(status_code=404, detail="Invalid UPI ID for receiver")
        print("receiver")

    # PIN verification
    if not bcrypt.verify(req.pin, sender.upi_pin):
        raise HTTPException(status_code=403, detail="Invalid UPI PIN")

    # Balance check
    if sender.balance < req.amount:
        raise HTTPException(status_code=400, detail="Insufficient balance")

    # Perform transaction
    sender.balance -= req.amount
    receiver.balance += req.amount
    txn = Transaction(
        from_upi=sender.upi_id,
        to_upi=receiver.upi_id,
        amount=req.amount,
        status="SUCCESS"
    )
    db.add(txn)
    db.commit()
    return {"success": True, "message": "Payment completed"}
@app.get("/upis")
def get_all_upis(db: Session = Depends(get_db)):
    users = db.query(User).all()
    return {"upis": [user.upi_id for user in users]}
