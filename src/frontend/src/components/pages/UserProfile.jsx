import { useContext, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthContext from '../../store/auth-context';
import MainHeading from '../layout/MainHeading';
import UserData from '../users/UserData';

const UserProfile = () => {
  const { currentUser } = useContext(AuthContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (!currentUser) {
      navigate('/login');
    }
  }, [currentUser, navigate]);

  return (
    <>
      <MainHeading title={`Your profile`} />
      <UserData user={currentUser} />
    </>
  );
};

export default UserProfile;
