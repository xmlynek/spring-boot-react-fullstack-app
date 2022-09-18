import Meta from 'antd/lib/card/Meta';
import { Button } from 'antd';
import { ShoppingCartOutlined } from '@ant-design/icons';
import ProductWrapper from './ProductWrapper';
import { useNavigate } from 'react-router-dom';

const ProductPreviewInfo = (props) => {
  const { product } = props;
  const navigate = useNavigate();

  const onCardClickHandler = () => {
    navigate(`${product.id}`);
  };

  const onAddToBasketHandler = (e) => {
    e.stopPropagation();
  };

  return (
    <ProductWrapper
      product={product}
      onClick={onCardClickHandler}
      hoverable
      style={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        maxWidth: 320,
        height: 440,
        maxHeight: 440,
        opacity: product.isAvailable ? 1 : 0.75
      }}>
      <Meta
        title={
          <p className="text-wrap">
            {`${product.name}`}
            {!product.isAvailable && (
              <span className="me-2" style={{ color: 'red' }}>
                {` - Product Unavailable`}
              </span>
            )}
          </p>
        }
        description={product.shortDescription}
      />
      <div
        className="mt-3 d-flex"
        style={{
          flexBasis: '100%',
          alignItems: 'flex-end',
          justifyContent: 'space-between',
          flexWrap: 'wrap'
        }}>
        <p className="float-start h4 mt-2">{product.price}$</p>
        <Button
          className="float-end"
          size="large"
          disabled={!product.isAvailable}
          onClick={onAddToBasketHandler}>
          <ShoppingCartOutlined />
          Add to basket
        </Button>
      </div>
    </ProductWrapper>
  );
};

export default ProductPreviewInfo;
