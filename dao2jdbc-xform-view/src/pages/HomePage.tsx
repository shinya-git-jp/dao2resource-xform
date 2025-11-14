import React from 'react';
import { Box, Typography, Paper, Divider } from '@mui/material';

const HomePage: React.FC = () => {
  return (
    <Box sx={{ p: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '80vh', bgcolor: '#f5f7fa' }}>
      <Paper elevation={3} sx={{ p: 5, maxWidth: 600, width: '100%' }}>
        <Box sx={{ textAlign: 'center', mb: 3 }}>
          <img src="/icon.png" alt="DAO2JDBC XFORM ロゴ" style={{ height: 100, marginBottom: 8 }} />
          <Typography variant="h4" fontWeight="bold" color="primary" gutterBottom>
            DAO2RESOURCE XFORM
          </Typography>
          <Typography variant="subtitle1" color="text.secondary">
            メッセージリソース・エラーメッセージリソースを変換するためのサービスです。
          </Typography>
        </Box>
        <Divider sx={{ mb: 3 }} />
        <Typography variant="h6" fontWeight="bold" gutterBottom>
          主な機能
        </Typography>
        <Typography variant="body1" paragraph>
          ・メッセージリソース変換<br />
          ・エラーメッセージリソース変換
        </Typography>
        <Typography variant="h6" fontWeight="bold" gutterBottom>
          ご利用手順
        </Typography>
        <Typography variant="body1" paragraph>
          1. 画面左上のヘッダーにあるハンバーガーメニュー（<b>≡</b>）をクリックしてください。<br />
          2. <br />
          3. <br />
          4. <br />
          5.
        </Typography>
        <Divider sx={{ my: 3 }} />
        <Typography variant="body2" color="text.secondary" align="center">
          Copyright © 2025 システム基盤課.
        </Typography>
      </Paper>
    </Box>
  );
};

export default HomePage;