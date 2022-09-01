import { useContext } from 'react';
import AuthContext from '../../store/auth-context';

const ProtectedComponent = (props) => {
  const authCtx = useContext(AuthContext);
  const { currentUser, roles } = authCtx;

  const hasAllowedRole = roles.some((role) => props.allowedRoles.includes(role));

  return <>{currentUser && roles && hasAllowedRole ? props.children : null}</>;
};

export default ProtectedComponent;
