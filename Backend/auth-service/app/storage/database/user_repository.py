from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.models.orm.user_orm import UserORM
from app.models.domain.user import User
from app.storage.database.base_repository import SessionLocal


class UserRepository:
    def create(self, email: str, password_hash: str) -> User:
        with SessionLocal() as session:
            db_user = UserORM(email=email, password_hash=password_hash)
            session.add(db_user)
            try:
                session.commit()
            except IntegrityError:
                session.rollback()
                raise ValueError("email already exists")

            session.refresh(db_user)
            return User(
                id=db_user.id,
                email=db_user.email,
                password_hash=db_user.password_hash,
            )

    def get_by_email(self, email: str) -> User | None:
        with SessionLocal() as session:
            db_user: UserORM | None = (
                session.query(UserORM)
                .filter(UserORM.email == email)
                .first()
            )

            if not db_user:
                return None

            return User(
                id=db_user.id,
                email=db_user.email,
                password_hash=db_user.password_hash,
            )
