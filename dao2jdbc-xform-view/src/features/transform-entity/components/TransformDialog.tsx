import '../../../styles/report.css';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid2';
import { Button, Card, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, Fab, FormControlLabel, IconButton, InputLabel, MenuItem, Select, Skeleton, TextField, Typography } from '@mui/material';
import { Close } from '@mui/icons-material';
import { useEntityTransformDialogData } from '../hooks/useEntityTransformDialogData';
import React from 'react';

 
const fadeInOutStyles = {
    animation: 'fadeInOut 2s infinite',
}; 
interface Props {
    conditions: EntityCondition[];
}
export const TransformDialog: React.FC<Props> = ({conditions})=> {
    const {
        openSettingDialog,
        setOpenSettingDialog,
        isCompleted,
        processBarLabel,
        processBar,
        searchJSON,
        changePaginationRange,
        convert,
        isConvertButtonDisabled,
        downloadFile,
        onSearch
    } = useEntityTransformDialogData(conditions);
    return (
        <div>
            <div id="header">
                <h1>Dao2Jdbc Entity XForm Report</h1>

                <div id="searchContainer">
                    <input type="text" id="searchByDescription" placeholder="エンティティIDで検索" onKeyUp={onSearch}/>

                    <select id="paginationNumber" onChange={changePaginationRange}>
                        <option value="5">1ページのアイテム数</option>
                        <option value="5">5アイテム</option>
                        <option value="10">10アイテム</option>
                        <option value="50">50アイテム</option>
                        <option value="100">100アイテム</option>
                        <option value="200">200アイテム</option>
                        <option value="500">500アイテム</option>
                        <option value="1000">1000アイテム</option>
                    </select>
                </div>
                <div id="page-info"></div>
            </div>

            <div id="container">
                <div id="pagination"></div>
                <div id="loading">
                    <Box  display="flex" flexDirection="column"  alignItems="center" justifyContent="center">
                        <Skeleton variant="rectangular" width={900} height={60} />
                        <Box height="10px"></Box>
                        <Skeleton variant="rectangular" width={900} height={60} />
                        <Box height="10px"></Box>
                        <Skeleton variant="rectangular" width={900} height={60} />
                    </Box>
                </div>
                <div id="jsonDisplay">
                </div>
            </div>

            <div id="notification">Copied to clipboard!</div>
            {processBar &&  <Box id="processBar" sx={{
                position: 'fixed',
                bottom: 16,
                left: 16, 
                zIndex: 1000, 
                paddingLeft: 5,
                paddingRight: 5,
                paddingTop: 2,
                paddingBottom: 2,
                backgroundColor: '#B71C1C',
                borderRadius: 2,
                color: 'white'
                }} style={fadeInOutStyles}>
                {processBarLabel}
            </Box>}
            {!isCompleted &&
            <Button aria-label="add" sx={{
                position: 'fixed',
                bottom: 16,
                right: 16, 
                zIndex: 1000, 
                padding: 3,
                fontSize: 20,
                color: "white",
                fontWeight: "bold",
                backgroundColor: "#B71C1C",
                borderRadius: 100,
                border: "20px #6e1717 solid",
                }}
                disabled={isConvertButtonDisabled}
                onClick={convert}
                >
                <span>変換</span>
                
            </Button>}
           {isCompleted && <Button aria-label="add" sx={{
                position: 'fixed',
                bottom: 16,
                right: 16, 
                zIndex: 1000, 
                padding: 3,
                fontSize: 12,
                color: "white",
                fontWeight: "bold",
                backgroundColor: "#B71C1C",
                borderRadius: 100,
                border: "20px #6e1717 solid",
                }}
                onClick={downloadFile}
                >
                <div>ダウンロード</div>
                
            </Button>}
            <Dialog
                open={openSettingDialog}
                onClose={()=>{setOpenSettingDialog(false)}}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                {<b>設定</b>}
                </DialogTitle>
                
                <IconButton
                aria-label="close"
                onClick={()=>{setOpenSettingDialog(false)}}
                sx={(theme) => ({
                    position: 'absolute',
                    right: 8,
                    top: 8,
                    color: theme.palette.grey[500],
                })}
                >
                <Close />
                </IconButton>
                <DialogContent>
                    <Box textAlign="center">
                        <Box display="flex" flexDirection="column">
                        <Grid container rowSpacing={1} columnSpacing={{ xs: 1, sm: 2, md: 3 }} >
                            <Grid size={6}>
                            <Card>
                                <Box display="flex" flexDirection="row">
                                <Box width="20px" display="flex" bgcolor="#B71C1C"/>
                                <Box padding="20px" textAlign="start" display="flex" flexDirection="column" flexGrow="1">
                                    <Typography fontSize="15pt" fontWeight="bold"> 
                                        locale設定
                                    </Typography>
                                    <Box marginTop="30px" textAlign="start">
                                        <InputLabel>locale: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='locale'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>country1: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='country1'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>country2: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='country2'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>country3: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='country3'
                                        fullWidth={true}
                                        />
                                    </Box>
                                        
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>country4: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='country4'
                                        fullWidth={true}
                                        />
                                    </Box>
                                        
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>country5: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='country4'
                                        fullWidth={true}
                                        />
                                    </Box>
                                </Box>
                                </Box>
                            </Card>
                            
                            <Box height="20px"/>
                            </Grid>
                            <Grid size={6}>
                            <Card>
                                <Box display="flex" flexDirection="row">
                                <Box width="20px" display="flex" bgcolor="#B71C1C"/>
                                <Box padding="20px" textAlign="start" display="flex" flexDirection="column" flexGrow="1">
                                    <Typography fontSize="15pt" fontWeight="bold"> 
                                        予約語設定
                                    </Typography>
                                    <Box marginTop="30px" textAlign="start">
                                        <InputLabel>Primaryキーカラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='Primaryキーカラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>Exclusiveキーカラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='Exclusiveキーカラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>会社コードカラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='会社コードカラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>保存者IDカラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='保存者IDカラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                        
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>保存日付カラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='保存日付カラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                        
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>更新者IDカラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='更新者IDカラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                    <Box marginTop="10px" textAlign="start">
                                        <InputLabel>更新日付カラム: </InputLabel>
                                        <TextField
                                        required
                                        id="outlined-required"
                                        placeholder='更新日付カラム'
                                        fullWidth={true}
                                        />
                                    </Box>
                                </Box>
                                </Box>
                            </Card>
                            </Grid>
                        </Grid>
                        </Box>
                    </Box>
                </DialogContent>
                <DialogActions>
                <Button onClick={()=>{setOpenSettingDialog(false)}} autoFocus>
                    適用
                </Button>
                </DialogActions>
            </Dialog>
            
            <div id="loadingOverlay" className="overlay">
                <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%'}}>
                    <div className="spinner"></div>
                    <div style={{ paddingLeft: '20px', paddingTop: '20px' }}>{processBarLabel}</div>
                </div>
            </div>
        </div>
    );
}
export default TransformDialog;