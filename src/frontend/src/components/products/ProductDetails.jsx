import { Button } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import ProductWrapper from './ProductWrapper';
import { useContext } from 'react';
import ProductContext from '../../store/products-context';
import { useNavigate } from 'react-router-dom';

const ProductDetails = (props) => {
  const { deleteProduct } = useContext(ProductContext);
  const { product } = props;
  const navigate = useNavigate();

  const onDeleteHandler = async (productId) => {
    await deleteProduct(productId);
    navigate('/products', { replace: true });
  };

  return (
    <ProductWrapper
      product={product}
      onDelete={onDeleteHandler}
      imgPreview
      coverImageProps={{ objectFit: 'cover', width: '100%', maxHeight: '600px' }}>
      <div style={{ fontSize: 17 }}>
        <p>
          {`Name: ${product.name}`}
          <span>{` - ${product.shortDescription}`}</span>
        </p>
        <p>{`Description: ${product.description}`}</p>
        <p>{`Pieces left: ${product.quantity}`}</p>
        <p>{`Product ID: ${product.id}`}</p>
        <p>
          {`Price: `}
          <span className="h4">{`${product.price}$`}</span>
          <Button
            className="float-end"
            size="large"
            style={{ backgroundColor: product.isAvailable ? 'lime' : '' }}
            disabled={!product.isAvailable}>
            <ShoppingCartOutlined />
            Add to basket
          </Button>
        </p>
      </div>
    </ProductWrapper>
  );
};

export default ProductDetails;
