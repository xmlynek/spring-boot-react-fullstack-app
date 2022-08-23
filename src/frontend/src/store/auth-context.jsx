import React, { useState } from 'react';

const AuthContext = React.createContext({
  currentUser: null,
  setCurrentUser: () => {},
  isAdmin: false
});

export const AuthContextProvider = (props) => {
  const [currentUser, setCurrentUser] = useState(null);
  const isAdmin = currentUser != null && currentUser.roles.includes('ADMIN');

  return (
    <AuthContext.Provider
      value={{ currentUser: currentUser, setCurrentUser: setCurrentUser, isAdmin: isAdmin }}>
      {props.children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
