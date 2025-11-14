import * as React from 'react';
import { createTheme } from '@mui/material/styles';
import SettingsIcon from '@mui/icons-material/Settings';
import { Navigation, Router } from '@toolpad/core/AppProvider';
import CodeIcon from '@mui/icons-material/Code';
import { Box, Container, IconButton, styled, Toolbar, Typography } from '@mui/material';
import TransformIcon from '@mui/icons-material/Transform';
import MenuIcon from '@mui/icons-material/Menu';
import MuiAppBar, { AppBarProps as MuiAppBarProps } from '@mui/material/AppBar';
import BackupTableIcon from '@mui/icons-material/BackupTable';
import MenuDrawer, { DrawerHeader } from '../components/MenuDrawer/MenuDrawer';
import { Outlet } from 'react-router-dom';



const NAVIGATION: Navigation = [
  {
    kind: 'header',
    title: 'メニュー',
  },
  {
    segment: 'settings',
    title: '変換設定管理',
    icon: <SettingsIcon />,
  },
  {
    segment: 'transform',
    title: '変換',
    icon: <TransformIcon />,
    children: [
      {
        segment: 'code',
        title: '仮想表',
        icon: <CodeIcon />,
      },
      {
        segment: 'entity',
        title: 'エンティティ',
        icon: <BackupTableIcon />,
      },
    ],
  },
];
const navigationTitle = {
  settings: {
    title: '設定管理',
    description: 'このページは、転換の設定を管理するページです。'
  },
  transform_code: {
    title: 'GEF-DAOの仮想表→GEF-JDBCのコード',
    description: 'このページは、仮想表をGEF-JDBCのコードに転換するためのインプットを入力するページです。'
  },
  transform_entity: {
    title: 'GEF-DAOのエンティティ→GEF-JDBCのエンティティファイル',
    description: 'このページは、エンティティ情報をGEF-JDBCのエンティティファイルに変換するためのインプットを入力するページです。'
  }
}

const drawerWidth = 380;
const Main = styled('main', { shouldForwardProp: (prop) => prop !== 'open' })<{
  open?: boolean;
}>(({ theme }) => ({
  flexGrow: 1,
  padding: theme.spacing(3),
  transition: theme.transitions.create('margin', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  marginLeft: `-${drawerWidth}px`,
  variants: [
    {
      props: ({ open }) => open,
      style: {
        transition: theme.transitions.create('margin', {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
        marginLeft: 0,
      },
    },
  ],
}));
interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
}
const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== 'open',
})<AppBarProps>(({ theme }) => ({
  transition: theme.transitions.create(['margin', 'width'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  variants: [
    {
      props: ({ open }) => open,
      style: {
        width: `calc(100% - ${drawerWidth}px)`,
        marginLeft: `${drawerWidth}px`,
        transition: theme.transitions.create(['margin', 'width'], {
          easing: theme.transitions.easing.easeOut,
          duration: theme.transitions.duration.enteringScreen,
        }),
      },
    },
  ],
}));
const demoTheme = createTheme({
  colorSchemes: { light: true, dark: true },
  cssVariables: {
    colorSchemeSelector: 'class',
  },
  breakpoints: {
    values: {
      xs: 0,
      sm: 600,
      md: 600,
      lg: 1200,
      xl: 1536,
    },
  },
});

function useDemoRouter(initialPath: string): Router {
  const [pathname, setPathname] = React.useState(initialPath);

  const router = React.useMemo(() => {
    return {
      pathname,
      searchParams: new URLSearchParams(),
      navigate: (path: string | URL) => setPathname(String(path)),
    };
  }, [pathname]);

  return router;
}

export const MainPage : React.FC = () => {
  const router = useDemoRouter('/transform/code');

  const [open, setOpen] = React.useState(false);
  const handleDrawerOpen = () => {
      setOpen(true);
  };
  const handleDrawerClose = () => {
    setOpen(false);
  };
  
  return (
    <>
    <Box sx={{ display: 'flex'}}>
       <MuiAppBar sx={{backgroundColor: "white", color: "#1b85d1"}}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={handleDrawerOpen}
            edge="start"
            sx={[
              {
                mr: 2,
              },
              open && { display: 'none' },
            ]}
          >
            <MenuIcon />
          </IconButton>
           <Box
            component="img"
            sx={{
              height: 50,
              marginRight: 2,
            }}
            alt="Logo"
            src="/icon.png" // Replace with your logo path
          />
          <Typography variant="h6" noWrap component="div">
            DAO2JDBC XFORM
          </Typography>
        </Toolbar>
      </MuiAppBar>
      <MenuDrawer open={open} onClose={handleDrawerClose}/>
      <Main open={open}>
        <Box
          flexGrow={1}>
            <DrawerHeader />
            <Container sx={{ marginTop: "40px", backgroundColor: "white", padding: "30px", borderRadius: 5,  boxShadow: "0 0px 0px 0 rgba(0, 0, 0, 0.2), 0 0px 5px 0 rgba(0, 0, 0, 0.19)"}}>
              <Outlet />
            </Container>
        </Box>
      </Main>
    </Box>
    </>
  );
}

export default MainPage;