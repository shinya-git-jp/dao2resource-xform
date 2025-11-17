import { Avatar, Box, Divider, Drawer, IconButton, List, ListItem, ListItemAvatar, ListItemButton, ListItemIcon, ListItemText, styled, Typography } from "@mui/material"
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { useTheme } from '@mui/material/styles';
import React from "react";
import { useNavigate } from "react-router-dom";
import SettingsIcon from '@mui/icons-material/Settings';
import HomeIcon from '@mui/icons-material/Home';
import CodeIcon from '@mui/icons-material/Code';
import BackupTableIcon from '@mui/icons-material/BackupTable';

const drawerWidth = 380;
export const DrawerHeader = styled('div')(({ theme }) => ({
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: 'flex-end',
}));
type MenuDrawerProps = {
    open: boolean,
    onClose: () => void
}
const MenuDrawer: React.FC<MenuDrawerProps> = ({ open, onClose }) => {
    const theme = useTheme();
    const navigate = useNavigate();
    return (
        <>
            <Drawer
                sx={{
                    width: drawerWidth,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: drawerWidth,
                        boxSizing: 'border-box',
                    },
                }}
                variant="persistent"
                anchor="left"
                open={open}
                role="presentation"
            >
                <DrawerHeader>
                    <Box display="flex" width={"100%"} flexDirection={"row"} paddingLeft={"10px"} alignItems={"left"} textAlign={"left"}>
                        <Box display="flex" flexDirection={"column"} alignItems={"center"} justifyContent={"center"}>
                            <Box
                                justifySelf={"center"}
                                component="img"
                                display={"flex"}
                                sx={{
                                    height: 50,
                                    marginRight: 2,
                                }}
                                alt="Logo"
                                src="/icon.png" // Replace with your logo path
                            />
                        </Box>
                        <Box display="flex" flexDirection={"column"} alignItems={"left"} justifyContent={"center"}>
                            <Typography fontSize={18} fontWeight={"bold"} sx={{ color: "#1b85d1" }}>
                                DAO2RESOURCE XFORM
                            </Typography>
                            {/* <Typography fontSize={14} fontWeight={"bold"} sx={{color: "#999797"}}>
                    © 2025, システム基盤課
                    </Typography> */}
                        </Box>
                    </Box>
                    <IconButton onClick={onClose}>
                        {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
                    </IconButton>
                </DrawerHeader>
                <Box paddingLeft={1}>
                    <List>
                        <ListItem key={"ホーム"} disablePadding>
                            <ListItemButton onClick={() => {
                                navigate("/");
                            }}>
                                <ListItemAvatar sx={{ minWidth: 40 }}>
                                    <HomeIcon color="primary" fontSize="medium" />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={<Box sx={{ color: "#1b85d1" }} fontWeight={"bold"}>ホーム</Box>}
                                    secondary={"DAO2RESOURCE XFORMの概要ページです。"} />
                            </ListItemButton>
                        </ListItem>
                        <ListItem key={"変換設定の管理"} disablePadding>
                            <ListItemButton onClick={() => {
                                navigate("/setting");
                            }}>
                                <ListItemAvatar sx={{ minWidth: 40 }}>
                                    <SettingsIcon color="primary" fontSize="medium" />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={<Box sx={{ color: "#1b85d1" }} fontWeight={"bold"}>変換設定の管理</Box>}
                                    secondary={"変換を実行するための設定を管理します。"} />
                            </ListItemButton>
                        </ListItem>
                        <ListItem key={"変換設定の管理"} disablePadding>
                            <ListItemButton onClick={() => {
                                navigate("/transform-code");
                            }}>
                                <ListItemAvatar sx={{ minWidth: 40 }}>
                                    <CodeIcon color="primary" fontSize="medium" />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={<Box sx={{ color: "#1b85d1" }} fontWeight={"bold"}>GEF-JDBCのAPIコード変換</Box>}
                                    secondary={"GEF-DAOの仮想表からGEF-JDBCのAPIコードに変換するためのページです。"}
                                />
                            </ListItemButton>
                        </ListItem>
                        <ListItem key={"変換設定の管理"} disablePadding>
                            <ListItemButton onClick={() => {
                                navigate("/transform-entity");
                            }}>
                                <ListItemAvatar sx={{ minWidth: 40 }}>
                                    <BackupTableIcon color="primary" fontSize="medium" />
                                </ListItemAvatar>
                                <ListItemText
                                    primary={<Box sx={{ color: "#1b85d1" }} fontWeight={"bold"}>GEFーJDBCのエンティティファイル変換</Box>}
                                    secondary={"GEF-DAOのエンティティからGEF-JDBCのエンティティファイルに変換するためのページです。"}
                                />
                            </ListItemButton>
                        </ListItem>
                    </List>
                </Box>

            </Drawer>
        </>
    );
}

export default MenuDrawer;