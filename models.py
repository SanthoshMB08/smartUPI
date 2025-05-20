from sqlalchemy import Column, String, Float, DateTime, ForeignKey
from sqlalchemy.dialects.sqlite import DATETIME
from sqlalchemy.orm import relationship
from database import Base
import uuid
from datetime import datetime

def gen_id():
    return str(uuid.uuid4())

class User(Base):
    __tablename__ = "users"
    id = Column(String, primary_key=True, default=gen_id)
    mobile = Column(String, unique=True, index=True)
    upi_id = Column(String, unique=True)
    password = Column(String)  # hashed
    upi_pin = Column(String)  # hashed UPI PIN
    balance = Column(Float, default=0.0)

class Transaction(Base):
    __tablename__ = "transactions"
    id = Column(String, primary_key=True, default=gen_id)
    from_upi = Column(String)
    to_upi = Column(String)
    amount = Column(Float)
    timestamp = Column(DATETIME, default=datetime.utcnow)
    status = Column(String)  # SUCCESS, FAILED
