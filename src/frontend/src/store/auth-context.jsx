import React, { useState } from 'react';

const AuthContext = React.createContext({
  currentUser: null,
  setCurrentUser: () => {},
  isAdmin: false,
  roles: []
});

export const AuthContextProvider = (props) => {
  const [currentUser, setCurrentUser] = useState(null);
  const isAdmin =
    currentUser !== null && currentUser.roles && currentUser.roles.includes('ROLE_ADMIN');
  const roles = currentUser && currentUser.roles ? currentUser.roles : [];

  return (
    <AuthContext.Provider
      value={{
        currentUser: currentUser,
        setCurrentUser: setCurrentUser,
        isAdmin: isAdmin,
        roles: roles
      }}>
      {props.children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
