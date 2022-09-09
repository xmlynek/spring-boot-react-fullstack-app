import { Result } from 'antd';
import { useContext, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import AuthContext from '../../store/auth-context';
import UsersContext from '../../store/users-context';
import MainHeading from '../layout/MainHeading';
import UserData from '../users/UserData';
import Spinner from '../UI/Spinner';

const UserByIdPage = () => {
  const { currentUser } = useContext(AuthContext);
  const { getUserById, userList } = useContext(UsersContext);
  const [isFetching, setIsFetching] = useState(true);

  const params = useParams();
  const navigate = useNavigate();

  const userId = params.userId;

  useEffect(() => {
    if (currentUser && currentUser.id == userId) {
      navigate('/profile', { replace: true });
    } else {
      getUserById(userId);
      setIsFetching(false);
    }
  }, []);

  if (isFetching) {
    return (
      <>
        <Spinner />
      </>
    );
  }

  if (userList.length === 0) {
    return (
      <Result
        status="404"
        title="404"
        subTitle={`Sorry, the user with id ${userId} does not exist.`}
        extra={
          <Link type="primary" to={'/users'}>
            Back to list of users
          </Link>
        }
      />
    );
  }

  return (
    <>
      <MainHeading title={`${userList[0].firstName}'s profile`} />
      <UserData user={userList[0]} />
    </>
  );
};

export default UserByIdPage;
