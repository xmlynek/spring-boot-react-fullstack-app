import { Card, Modal, Popconfirm } from 'antd';
import { DeleteOutlined, SettingOutlined } from '@ant-design/icons';
import { useContext, useState } from 'react';
import ProductContext from '../../store/products-context';
import ProductForm from './ProductForm';
import ProtectedComponent from '../auth/ProtectedComponent';

const ProductWrapper = (props) => {
  const { state, deleteProduct, updateProduct } = useContext(ProductContext);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const { product } = props;

  const onEditClickHandler = (e) => {
    e.stopPropagation();
    setIsModalVisible(true);
  };

  const onDeleteClickHandler = async (e) => {
    e.stopPropagation();
    props.onDelete ? await props.onDelete(product.id) : await deleteProduct(product.id);
  };

  const onUpdateSubmitHandler = async (productId, values) => {
    props.onUpdate
      ? await props.onUpdate(productId, values)
      : await updateProduct(productId, values);
    setIsModalVisible(false);
  };

  return (
    <>
      <Modal
        title={'Update product'}
        footer={false}
        visible={isModalVisible}
        onCancel={() => setIsModalVisible(false)}>
        <ProductForm
          onFinish={onUpdateSubmitHandler.bind(null, product.id)}
          productData={product}
          visible={isModalVisible}
          submitBtnTitle={'Save changes'}
        />
      </Modal>
      <Card
        hoverable={props.hoverable}
        style={props.style}
        onClick={props.onClick}
        bodyStyle={{
          padding: 15,
          height: '40%',
          marginBottom: '5px',
          display: 'flex',
          flexDirection: 'column'
        }}
        cover={
          <img
            alt="image"
            src="https://images.unsplash.com/photo-1536782376847-5c9d14d97cc0?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8ZnJlZXxlbnwwfHwwfHw%3D&w=1000&q=80"
          />
        }
        actions={[
          <ProtectedComponent key={'edit'} allowedRoles={['ROLE_ADMIN']}>
            <SettingOutlined onClick={onEditClickHandler} />
          </ProtectedComponent>,
          <ProtectedComponent key={'delete'} allowedRoles={['ROLE_ADMIN']}>
            <Popconfirm
              placement="topRight"
              okButtonProps={{
                loading: state.isLoading
              }}
              title={`Are you sure to delete product '${product.name}'?`}
              onConfirm={onDeleteClickHandler}
              onCancel={(event) => event.stopPropagation()}
              okText="Yes"
              cancelText="No">
              <DeleteOutlined onClick={(e) => e.stopPropagation()} />
            </Popconfirm>
          </ProtectedComponent>
        ]}>
        {props.children}
      </Card>
    </>
  );
};

export default ProductWrapper;
