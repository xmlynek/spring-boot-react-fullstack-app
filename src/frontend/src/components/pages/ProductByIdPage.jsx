import { useEffect } from 'react';
import { useContext } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';

import Spinner from '../UI/Spinner';
import ProductContext from '../../store/products-context';
// import MainHeading from '../layout/MainHeading';
import { State } from '../../hooks/useAxiosRequest';
import { Modal, Result } from 'antd';
import ProductDetails from '../products/ProductDetails';

const ProductByIdPage = () => {
  const { getProductById, currentProductById, state } = useContext(ProductContext);
  const params = useParams();
  const navigate = useNavigate();

  const productId = params.productId;

  useEffect(() => {
    getProductById(productId);
  }, []);

  let output;

  if (state.isLoading && state.status === State.PENDING) {
    output = <Spinner />;
  }

  if (!state.isLoading && state.status === State.SUCCESS && currentProductById) {
    output = (
      <>
        {!currentProductById.isAvailable && (
          <p className="text-center h2 mb-3" style={{ color: 'red' }}>
            This product is not available
          </p>
        )}
        <ProductDetails product={currentProductById} />
      </>
    );
  }

  if (!currentProductById && !state.isLoading && state.status === State.ERROR) {
    output = (
      <Result
        status="404"
        title="404"
        subTitle={`Sorry, the product with id ${productId} does not exist.`}
        extra={
          <Link type="primary" to={'/products'}>
            Back to list of products
          </Link>
        }
      />
    );
  }

  return (
    <Modal
      title={'Product details'}
      visible={true}
      footer={false}
      onCancel={() => navigate('/products')}>
      {output}
    </Modal>
  );
};

export default ProductByIdPage;
