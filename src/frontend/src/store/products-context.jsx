import React, { useState } from 'react';
import { useEffect } from 'react';
import { errorNotification, successNotification } from '../components/UI/Notification';
import useAxiosRequest, { State } from '../hooks/useAxiosRequest';

const ProductContext = React.createContext({
  productList: [],
  currentProductById: null,
  state: {},
  fetchProducts: () => {},
  deleteProduct: () => {},
  updateProduct: () => {},
  createProduct: () => {},
  getProductById: () => {}
});

export const ProductContextProvider = (props) => {
  const [productList, setProductList] = useState([]);
  const [currentProductById, setCurrentProductById] = useState(null);
  const [state, sendRequest] = useAxiosRequest();

  useEffect(() => {
    if (state && state.error && state.errMsg && state.status === State.ERROR) {
      errorNotification('Error occurred', state.errMsg);
    }
  }, [state]);

  const fetchProductsHandler = async () => {
    await sendRequest({ url: '/api/v1/products' }, (data) => setProductList(() => data));
  };

  const getProductByIdHandler = async (productId) => {
    await sendRequest({ url: `/api/v1/products/${productId}` }, setCurrentProductById);
  };

  const createProductHandler = async (data) => {
    await sendRequest({ url: `/api/v1/products`, method: 'POST', data: data }, (data) => {
      successNotification(
        `Product created successfully`,
        `Product ${data.name} was successfully created`
      );
      fetchProductsHandler();
    });
  };

  const deleteProductHandler = async (productId) => {
    await sendRequest({ url: `/api/v1/products/${productId}`, method: 'DELETE' }, () => {
      successNotification(
        `Product deleted successfully`,
        `Product with id ${productId} was successfully deleted`
      );
      fetchProductsHandler();
    });
  };

  const updateProductHandler = async (productId, data) => {
    await sendRequest(
      { url: `/api/v1/products/${productId}`, method: 'PUT', data: data },
      (res) => {
        successNotification(
          `Product updated successfully`,
          `Product ${res.name} was successfully updated`
        );
        fetchProductsHandler();
        getProductByIdHandler(productId);
      }
    );
  };

  return (
    <ProductContext.Provider
      value={{
        productList: productList,
        currentProductById: currentProductById,
        state: state,
        fetchProducts: fetchProductsHandler,
        getProductById: getProductByIdHandler,
        createProduct: createProductHandler,
        deleteProduct: deleteProductHandler,
        updateProduct: updateProductHandler
      }}>
      {props.children}
    </ProductContext.Provider>
  );
};

export default ProductContext;
