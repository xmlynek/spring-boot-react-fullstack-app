import CustomColumnRows from '../utils/CustomColumnRows';

const UserData = (props) => {
  const { user } = props;

  return (
    <>
      {Object.keys(user).map((key, index) => (
        <CustomColumnRows
          key={index}
          title={key.charAt(0).toUpperCase() + key.slice(1)}
          value={key === 'roles' ? `[ ${user[key].map((role) => ` ${role}`)} ]` : user[key]}
        />
      ))}
    </>
  );
};

export default UserData;
