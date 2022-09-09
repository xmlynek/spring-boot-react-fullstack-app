import { Col, Row } from 'antd';

const CustomColumnRows = (props) => {
  const gutter = props.gutter ? props.gutter : [16, 16];
  const colSpan = props.colSpan ? props.colSpan : 6;
  const colOffset = props.colOffset ? props.colOffset : colSpan;

  return (
    <Row gutter={gutter}>
      <Col span={colSpan} offset={colOffset} className={'text-end'}>
        <p>{props.title}</p>
      </Col>
      <Col span={colSpan * 2}>
        <p>{`${props.value}`}</p>
      </Col>
    </Row>
  );
};

export default CustomColumnRows;
