import { Card, Image, Modal, Popconfirm } from 'antd';
import { DeleteOutlined, SettingOutlined } from '@ant-design/icons';
import { useContext, useState } from 'react';
import ProductContext from '../../store/products-context';
import ProductForm from './ProductForm';
import ProtectedComponent from '../auth/ProtectedComponent';

import defaultImage from '../../assets/images/default-image.jpg';

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
          update
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
          <Image
            preview={props.imgPreview ? props.imgPreview : false}
            alt="image"
            src={
              product.productImage
                ? `data:image/png;base64,${product.productImage.data}`
                : defaultImage
            }
            style={
              props.coverImageProps
                ? props.coverImageProps
                : { objectFit: 'cover', width: '100%', height: '220px' }
            }
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
