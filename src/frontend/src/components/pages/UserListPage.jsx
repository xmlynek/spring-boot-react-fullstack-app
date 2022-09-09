import MainHeading from '../layout/MainHeading';
import UserList from '../users/UserList';

const UserListPage = () => {
  return (
    <>
      <MainHeading title={'List of users'} />
      <UserList />
    </>
  );
};

export default UserListPage;
