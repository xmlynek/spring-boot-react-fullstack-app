import { useContext } from 'react';
import AuthContext from '../../store/auth-context';

const UserProfile = () => {
  const { currentUser } = useContext(AuthContext);

  return <>{JSON.stringify(currentUser)}</>;
};

export default UserProfile;
