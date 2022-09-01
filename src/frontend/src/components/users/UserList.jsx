import { useContext, useEffect, useState } from 'react';
import { Table, Button, Popconfirm, Space, Tag, Badge } from 'antd';
import { PlusOutlined, CheckSquareTwoTone, CloseSquareTwoTone } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

import Modal from 'antd/lib/modal/Modal';
import UserForm from './UserForm';
import UsersContext from '../../store/users-context';
import classes from './UserList.module.css';

const UserList = () => {
  const navigate = useNavigate();
  const usersCtx = useContext(UsersContext);
  const { userList, createUser, fetchUsers, deleteUser, updateUser } = usersCtx;

  const [isModalVisible, setIsModalVisible] = useState({ isVisible: false, modalType: 'EDIT' });
  const [userToUpdate, setUserToUpdate] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, []);

  const onDeleteConfirmHandler = (userId) => {
    deleteUser(userId);
    fetchUsers();
  };

  const onCreateConfirmHandler = (userData) => {
    createUser(userData);
    fetchUsers();
    setIsModalVisible({ isVisible: false });
  };

  const onUpdateConfirmHandler = (userId, userData) => {
    updateUser(userId, userData);
    fetchUsers();
    setIsModalVisible({ isVisible: false });
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      fixed: 'left',
      sorter: (a, b) => a.id - b.id
    },
    {
      title: 'Firstname',
      dataIndex: 'firstname',
      key: 'firstname',
      sorter: (a, b) => a.firstname.localeCompare(b.firstname)
    },
    {
      title: 'Lastname',
      dataIndex: 'lastname',
      key: 'lastname',
      sorter: (a, b) => a.lastname.localeCompare(b.lastname)
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      sorter: (a, b) => a.email.localeCompare(b.email)
    },
    {
      title: 'Gender',
      dataIndex: 'gender',
      key: 'gender',
      width: 120,
      sorter: (a, b) => a.gender.localeCompare(b.gender)
    },
    {
      title: 'Birthdate',
      dataIndex: 'birthdate',
      key: 'birthdate',
      width: 140,
      sorter: (a, b) => new Date(a.birthdate) - new Date(b.birthdate)
    },
    {
      title: 'Roles',
      dataIndex: 'roles',
      key: 'roles',
      sorter: (a, b) => a.roles.localeCompare(b.roles)
    },
    {
      title: 'Enabled',
      dataIndex: 'isEnabled',
      key: 'isEnabled',
      width: 80,
      sorter: (a, b) => a.isEnabled - b.isEnabled,
      render: (text, user) => {
        return user.isEnabled ? (
          <CheckSquareTwoTone style={{ fontSize: '25px' }} />
        ) : (
          <CloseSquareTwoTone style={{ fontSize: '25px' }} />
        );
      }
    },
    {
      title: 'Actions',
      dataIndex: 'actions',
      key: 'actions',
      render: (text, user) => (
        <Space size={'small'} wrap>
          <Popconfirm
            placement="topRight"
            title={`Are you sure to delete ${user.firstname}?`}
            onConfirm={(event) => {
              event.stopPropagation();
              onDeleteConfirmHandler(user.id);
            }}
            onCancel={(event) => event.stopPropagation()}
            okText="Yes"
            cancelText="No">
            <Button danger style={{ width: '65px' }} onClick={(event) => event.stopPropagation()}>
              Delete
            </Button>
          </Popconfirm>
          <Button
            type={'primary'}
            style={{ width: '65px' }}
            onClick={(event) => {
              event.stopPropagation();
              setIsModalVisible({ isVisible: true, modalType: 'EDIT' });
              setUserToUpdate(() => userList.find((usr) => usr.id === user.id));
            }}>
            Edit
          </Button>
        </Space>
      )
    }
  ];

  const data = userList.map((user) => ({
    key: user.id,
    id: user.id,
    firstname: user.firstName,
    lastname: user.lastName,
    email: user.email,
    gender: user.gender,
    birthdate: user.birthDate,
    isEnabled: user.isEnabled,
    roles: `[ ${user.roles.map((role) => ` ${role}`)} ]`
  }));

  return (
    <>
      <Modal
        title={isModalVisible.modalType === 'EDIT' ? 'Edit user' : 'Create user'}
        visible={isModalVisible.isVisible}
        footer={null}
        onCancel={() => {
          setIsModalVisible({ isVisible: false });
          setUserToUpdate(() => null);
        }}>
        {isModalVisible.modalType === 'EDIT' && (
          <UserForm
            userData={userToUpdate}
            userRolesField
            isEnabledField
            onSubmit={onUpdateConfirmHandler.bind(null, userToUpdate ? userToUpdate.id : 0)}
            submitBtnTitle={'Confirm changes'}
          />
        )}
        {isModalVisible.modalType === 'CREATE' && (
          <UserForm passwordField userRolesField isEnabledField onSubmit={onCreateConfirmHandler} />
        )}
      </Modal>
      <Table
        columns={columns}
        dataSource={data}
        rowClassName={classes.tableRow}
        onRow={(record) => {
          return {
            onClick: () => {
              navigate(`/users/${record.id}`);
            }
          };
        }}
        title={() => (
          <>
            <Button
              type="primary"
              shape="round"
              onClick={() => setIsModalVisible({ isVisible: true, modalType: 'CREATE' })}
              icon={<PlusOutlined />}
              size="large">
              Add new user
            </Button>
            <div className="float-end">
              <Tag style={{ marginLeft: '10px' }}>
                Number of users:
                <Badge count={userList ? userList.length : 0} className="site-badge-count-4 ms-2" />
              </Tag>
            </div>
          </>
        )}
        pagination={{ pageSize: 25 }}
        scroll={{ y: 500, x: 1200 }}
      />
    </>
  );
};

export default UserList;
