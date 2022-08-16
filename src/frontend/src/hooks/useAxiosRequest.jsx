import axios from 'axios';
import { useCallback, useReducer } from 'react';

export const State = {
  SUCCESS: 'SUCCESS',
  PENDING: 'PENDING',
  ERROR: 'ERROR',
  NONE: 'NONE'
};

const reducer = (state, action) => {
  let newState = state;
  switch (action.type) {
    case State.SUCCESS:
      newState = {
        isLoading: false,
        error: false,
        status: State.SUCCESS,
        errMsg: null,
        data: action.data
      };
      break;
    case State.PENDING:
      newState = { isLoading: true, error: false, status: State.PENDING, errMsg: null, data: null };
      break;
    case State.ERROR:
      newState = {
        isLoading: false,
        error: true,
        status: State.ERROR,
        errMsg: action.errMsg,
        data: null
      };
      break;
  }
  return newState;
};

function useAxiosRequest() {
  const [state, dispatch] = useReducer(reducer, {
    isLoading: false,
    error: false,
    status: State.NONE,
    errMsg: null,
    data: null
  });

  const sendRequest = useCallback(async (requestConfig, applyData = () => {}) => {
    dispatch({ type: State.PENDING });
    axios
      .request(requestConfig)
      .then((res) => {
        dispatch({ type: State.SUCCESS, data: res.data });
        applyData(res.data);
      })
      .catch((err) => {
        dispatch({ type: State.ERROR, errMsg: err.message });
      });
  }, []);

  return [state, sendRequest];
}

export default useAxiosRequest;
