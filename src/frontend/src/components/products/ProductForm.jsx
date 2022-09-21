import { Button, Checkbox, Form, Input, Switch, Upload } from 'antd';
import TextArea from 'antd/lib/input/TextArea';
import { useEffect, useState } from 'react';

const ProductForm = (props) => {
  const [form] = Form.useForm();
  const [fileList, setFileList] = useState([]);
  const [isProductImageInputShown, setIsProductImageInputShown] = useState(true);

  useEffect(() => {
    form.resetFields();
    setFileList([]);
    if (props.productData) {
      form.setFieldValue(props.productData);
      form.setFieldValue('productImage', null);
    }
  }, [props]);

  const handlePreview = async (file) => {
    let src = file.url;

    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);

        reader.onload = () => resolve(reader.result);
      });
    }

    const image = new Image();
    image.src = src;
    const imgWindow = window.open(src);
    imgWindow?.document.write(image.outerHTML);
  };

  const onImageFileChange = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  return (
    <>
      <Form
        form={form}
        labelCol={{ span: 7 }}
        wrapperCol={{ span: 15 }}
        initialValues={props.productData ? props.productData : {}}
        onFinish={(values) => {
          if (values.isAvailable === undefined) {
            values.isAvailable = false;
          }
          props.onFinish(values);
          form.resetFields();
        }}>
        <Form.Item disabled label={`${props.update ? 'Edit' : 'Add'} product image`}>
          <Switch defaultChecked onChange={(checked) => setIsProductImageInputShown(checked)} />
        </Form.Item>
        {isProductImageInputShown && (
          <Form.Item
            label="Product image"
            name="productImage"
            rules={[
              {
                required: true,
                message: 'Please add the product image'
              }
            ]}>
            <Upload
              maxCount={1}
              name="productImage"
              listType="picture-card"
              onChange={onImageFileChange}
              fileList={fileList}
              onPreview={handlePreview}
              beforeUpload={() => false}>
              {fileList.length < 1 && '+ Upload'}
            </Upload>
          </Form.Item>
        )}
        <Form.Item
          label="Product name"
          name="name"
          rules={[
            { required: true, message: 'Please input product name!' },
            { max: 128, message: 'Product name can not contain more than 128 characters' }
          ]}>
          <Input placeholder="Product name" showCount />
        </Form.Item>
        <Form.Item
          label="Short description"
          name="shortDescription"
          rules={[
            { required: true, message: 'Please input short description!' },
            { max: 40, message: 'Short description can not contain more than 40 characters' }
          ]}>
          <Input placeholder="Short description" showCount />
        </Form.Item>
        <Form.Item
          label="Description"
          name="description"
          rules={[
            { required: true, message: 'Please, fill the description!' },
            { max: 1024, message: 'Description can not contain more than 1024 characters' }
          ]}>
          <TextArea
            name="description"
            placeholder="Description"
            showCount
            autoSize={{ minRows: 3, maxRows: 7 }}
          />
        </Form.Item>
        <Form.Item
          label="Quantity"
          name="quantity"
          rules={[{ required: true, message: 'Please input the quantity' }]}>
          <Input type="number" name="quantity" placeholder="20" min={0} step={1} />
        </Form.Item>
        <Form.Item
          label="Price"
          name="price"
          rules={[{ required: true, message: 'Please input product price' }]}>
          <Input type="number" placeholder="20.54" min={0.0} step={0.01} />
        </Form.Item>
        <Form.Item label="Is available" name="isAvailable" valuePropName="checked">
          <Checkbox name="isAvailable" />
        </Form.Item>
        <div className="text-center mt-2">
          <Button
            type="primary"
            htmlType="submit"
            className="login-form-button width-responsive"
            loading={false}>
            {props.submitBtnTitle ? props.submitBtnTitle : 'Save'}
          </Button>
        </div>
      </Form>
    </>
  );
};

export default ProductForm;
