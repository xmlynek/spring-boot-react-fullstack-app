import { Layout } from 'antd';
import { Content, Footer, Header } from 'antd/lib/layout/layout';
import React from 'react';
import MainNavigation from './MainNavigation';
import classes from './Layout.module.css';

const PageLayout = (props) => (
  <Layout className="layout" style={{ minHeight: '100vh' }}>
    <Header>
      <MainNavigation />
    </Header>
    <Content
      style={{
        padding: '20px 50px'
      }}>
      <div className={classes.siteLayoutContent}>{props.children}</div>
    </Content>
    <Footer
      style={{
        textAlign: 'center'
      }}>
      Ant Design © 2022 Filip Mlýnek
    </Footer>
  </Layout>
);

export default PageLayout;
