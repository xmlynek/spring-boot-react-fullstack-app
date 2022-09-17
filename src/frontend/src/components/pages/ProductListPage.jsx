import { Outlet } from 'react-router-dom';
import { Button, Modal } from 'antd';
import ProductList from '../products/ProductList';
import MainHeading from '../layout/MainHeading';
import ProductForm from '../products/ProductForm';
import { useEffect, useState } from 'react';
import { useContext } from 'react';
import ProductContext from '../../store/products-context';

const ProductListPage = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const { createProduct, fetchProducts, productList } = useContext(ProductContext);

  useEffect(() => {
    fetchProducts();
  }, []);

  const onFinishHandler = async (values) => {
    setIsModalVisible(false);
    await createProduct(values);
  };

  return (
    <>
      <Modal
        title={'Create new product'}
        footer={false}
        visible={isModalVisible}
        onCancel={() => setIsModalVisible(false)}>
        <ProductForm onFinish={onFinishHandler} visible={isModalVisible} />
      </Modal>
      <MainHeading title={`Products`} />
      <div className="text-center mb-3">
        <Button size="large" onClick={() => setIsModalVisible(true)}>
          New product
        </Button>
      </div>
      <ProductList productList={productList} />
      <Outlet />
    </>
  );
};

export default ProductListPage;
