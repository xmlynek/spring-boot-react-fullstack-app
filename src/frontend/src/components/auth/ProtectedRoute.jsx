import { Result } from 'antd';
import { Link } from 'react-router-dom';
import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import AuthContext from '../../store/auth-context';

const ProtectedRoute = (props) => {
  const authCtx = useContext(AuthContext);
  const { currentUser, roles } = authCtx;

  const hasAllowedRole = roles.some((role) => props.allowedRoles.includes(role));

  if (currentUser && currentUser.roles && hasAllowedRole) return <>{props.children}</>;

  if (currentUser && currentUser.roles && !hasAllowedRole) {
    return (
      <Result
        status="403"
        title="403"
        subTitle="Sorry, you are not authorized to access this page."
        extra={
          <Link type="primary" to={'home'}>
            Back Home
          </Link>
        }
      />
    );
  }

  return <Navigate to={props.redirectPath ? props.redirectPath : '/login'} />;
};

export default ProtectedRoute;
