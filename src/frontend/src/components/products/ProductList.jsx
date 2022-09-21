import { Col, Row } from 'antd';
import ProductPreviewInfo from './ProductPreviewInfo';

const ProductList = (props) => {
  const { productList } = props;

  if (!productList || productList.length === 0) {
    return <p className="text-center h3">Product list is empty.</p>;
  }

  return (
    <Row wrap gutter={32} justify="center">
      {productList.map((product, index) => (
        <Col key={index} className={'mt-4'} style={{ maxWidth: 'none' }}>
          <ProductPreviewInfo product={product} />
        </Col>
      ))}
    </Row>
  );
};

export default ProductList;
