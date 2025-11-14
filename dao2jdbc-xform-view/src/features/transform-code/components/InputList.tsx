import * as React from 'react';
import Box from '@mui/material/Box';
import { Menu, MenuItem, Button, Checkbox, Dialog, Grid2, IconButton, InputLabel, List, ListItem, ListItemButton, ListItemText, Select, TextField, Toolbar, Typography } from '@mui/material';
import ConfirmationPage from './TransformDialog';
import { Close } from '@mui/icons-material';
import { InputDialog } from './InputDialog';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { v4 as uuidv4 } from 'uuid';
import { useInputListPageData } from '../hooks/useInputListPageData';
import { CustomTreeItem, readYaml, TitleTreeItem } from '../../../utils/utils';
import { ScriptType } from '../../../types/enum';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import { RequestManager } from '../../../utils/RequestManager';
import { checkSettingConnection } from '../../../services/setting-service';

export const InputListPage: React.FC = () => {
  const {
    open,
    setOpen,
    openPreview,
    setOpenPreview,
    settings,
    setGlobalSetting,
    tempGlobalSetting,
    setTempGlobalSetting,
    scripts,
    setScript,
    removeScript,
    scriptsChecked,
    setScriptsChecked,
    searchTerm,
    setSearchTerm,
    crudTypeTerm,
    setCrudTypeTerm,
    addScript,
    script
  } = useInputListPageData();
  
  const [tabIndex, setTabIndex] = React.useState('1');
  const handleTabChange = (event: React.SyntheticEvent, newValue: string) => {
    setTabIndex(newValue);
  };
  const fileInputRef = React.useRef(null);
  const handleImportFile = () => {
    fileInputRef.current.click();
  };
  
    const parsedData = (data: any) => {
      try {
        let parseFlag = "";
        try {
          for (const item of data) {
            let tempItem = null;
            let crudTypeSelect = false;
            let crudTypeDelete = false;
            let crudTypeInsert = false;
            let crudTypeUpdate = false;
            if (item.type == undefined || item.type == null) {
              crudTypeSelect = true;
              crudTypeDelete = true;
              crudTypeInsert = true;
              crudTypeUpdate = true;
            } else {
              crudTypeSelect = item.type.includes("SELECT");
              crudTypeDelete = item.type.includes("DELETE");
              crudTypeInsert = item.type.includes("INSERT");
              crudTypeUpdate = item.type.includes("UPDATE");
            }
            if (item.veId != undefined) {
              
              tempItem = {
                id: uuidv4(),
                crudTypeDelete: crudTypeDelete,
                crudTypeInsert: crudTypeInsert,
                crudTypeSelect: crudTypeSelect,
                crudTypeUpdate: crudTypeUpdate,
                description: item.veId,
                type: ScriptType.VEID,
                setting: null
              };
            } else if(item.categoryId != undefined) {
                tempItem = {
                  id: uuidv4(),
                  crudTypeDelete: crudTypeDelete,
                  crudTypeInsert: crudTypeInsert,
                  crudTypeSelect: crudTypeSelect,
                  crudTypeUpdate: crudTypeUpdate,
                  description: item.categoryId,
                  type: ScriptType.CATEGORYID,
                  setting: null
                };
            } else {
              parseFlag = "フォーマットは違います。"
              alert(parseFlag);
              break;
            }
            
              addScript(tempItem);
            }
            if (parseFlag == "") {
             alert("インポートが成功しました。");
            }
          } catch (error) {
            alert('フォーマットは違います。');
          }
      } catch(exception) {
        alert("フォーマットは違います。");
      }
    }
    
  const importInputs = (event) => {
    const file = event.target.files[0];
    if (file) {
      readYaml(file, parsedData);
    }
  };
  const onChangeGlobalSetting = (event: React.ChangeEvent<HTMLInputElement>) => {
    let setting = event.target.value;
    if (event.target.value == "") {
      setting = null;
    }
    const tempSetting = settings.find((item: Setting) => item.id == setting);
    setGlobalSetting(tempSetting);
    setTempGlobalSetting(tempSetting);
  }
  
  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    RequestManager.cancelAll();
  };
  
  const showLoading = () => {
    const overlay = document.getElementById('loadingOverlay2');
    if (overlay) {
        overlay.style.display = 'flex';
    }
  }
  const hideLoading = () => {
      const overlay = document.getElementById('loadingOverlay2');
      if (overlay) {
          overlay.style.display = 'none';
      }
  }
  const checkConnection = async () => {
    showLoading();
    try {
      const isSuccess = await checkSettingConnection(tempGlobalSetting);
      if (isSuccess) {
        hideLoading();
        return true;
      }
      alert("選択された変換設定で接続が失敗です。");
    } catch (error) {
      console.error("Error:", error);
      alert("選択された変換設定で接続が失敗です。");
    }
    hideLoading();
    return false;
  }
  const handleClickOpenPreview = async () => {
    if (tempGlobalSetting == undefined || tempGlobalSetting == null) {
      alert("設定を選んでください。")
      return;
    }
    
    if (scripts.length == 0) {
      if (confirm("条件が空です。全ての仮想表が対象になります。続けますか?")) {
        if (await checkConnection()) {
          setOpenPreview(true);
        }
      } 
    } else {
      if (await checkConnection()) {
        setOpenPreview(true);
      }
    }
  };
  const handleClickClosePreview = () => {
    setOpenPreview(false);
    RequestManager.cancelAll();
  };
  const openScriptDetails = (id: string) => {
    const script = scripts.find((script: Script) => script.id == id);
    if (script) {
      setScript(script);
      handleClickOpen();
    }
  }
  const addNewScript = () => {
    setScript({
      id: uuidv4(), 
      description: "",
      type: ScriptType.VEID,
      crudTypeSelect: false,
      crudTypeDelete: false,
      crudTypeInsert: false,
      crudTypeUpdate: false,
      setting: null
    });
    handleClickOpen();
  }
  
  const isSelectedScriptExist = () : boolean => {
    return Object.values(scriptsChecked).every(element => !element);
  }
  const deleteSubjectScripts = () => {
    const userConfirmed = window.confirm("Are you sure you want to delete?");
    if (userConfirmed) {
      Object.keys(scriptsChecked).forEach((key)=> {
        if (scriptsChecked[key]) {
          removeScript(key);
        }
      })
      setScriptsChecked([])
    }
  }
  const filteredScripts = scripts.filter((script) => {
      const searchValue = searchTerm.toLowerCase();
      const isIncluded = script.description.toLowerCase().includes(searchValue);
      let isIncludedCrudType = true;
      const crudTypeValue = crudTypeTerm.toLowerCase();
      if (crudTypeValue == "select") {
        isIncludedCrudType = script.crudTypeSelect;
      } else if (crudTypeValue == "update") {
        isIncludedCrudType = script.crudTypeUpdate;
      } else if (crudTypeValue == "delete") {
        isIncludedCrudType = script.crudTypeDelete;
      } else if (crudTypeValue == "insert") {
        isIncludedCrudType = script.crudTypeInsert;
      }
      if (!isIncluded || !isIncludedCrudType) {
        scriptsChecked[script.id] = false;
      }
      return isIncluded && isIncludedCrudType; 
    }
  );

  const addItem = (script: Script) => {
    if (!script.crudTypeDelete && !script.crudTypeInsert && !script.crudTypeSelect && !script.crudTypeUpdate) {
      script.crudTypeDelete = true;
      script.crudTypeInsert = true;
      script.crudTypeSelect = true;
      script.crudTypeUpdate = true;
    }
    const tempScript = scripts.find((s: Script)=> script.id == s.id);
    if (tempScript) {
        tempScript.description = script.description;
        tempScript.crudTypeDelete = script.crudTypeDelete;
        tempScript.crudTypeInsert = script.crudTypeInsert;
        tempScript.crudTypeSelect = script.crudTypeSelect;
        tempScript.crudTypeUpdate = script.crudTypeUpdate;
        tempScript.setting = script.setting;
        tempScript.type = script.type;
    } else {
        addScript({
            id: script.id,
            description: script.description,
            crudTypeDelete: script.crudTypeDelete,
            crudTypeInsert: script.crudTypeInsert,
            crudTypeSelect: script.crudTypeSelect,
            crudTypeUpdate: script.crudTypeUpdate,
            setting: script.setting,
            type: script.type
        });
    }
    alert("保存しました。");
    resetScript();
    handleClose();
  }
  const resetScript = () => {
    setScript({
      id: "",
      description: "",
      type: ScriptType.VEID,
      crudTypeSelect: false,
      crudTypeDelete: false,
      crudTypeInsert: false,
      crudTypeUpdate: false,
      setting: null
    });
  }
  
  const [anchorElMenu, setAnchorElMenu] = React.useState<null | HTMLElement>(null);
  const menuOpen = Boolean(anchorElMenu);
  const handleClickMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElMenu(event.currentTarget);
  };
  const handleCloseMenu = () => {
    setAnchorElMenu(null);
  };
  
  return (
    <Box textAlign="center" paddingBottom="50px" >
      <Box display="flex">
        <Box sx={{marginBottom: "10px", textAlign: "left"}}>
          <Typography variant="h6" textTransform="uppercase" fontWeight="bold" >GEF-JDBCのAPIコード変換ページ</Typography>
          <Typography gutterBottom textAlign={"left"} fontSize={16} sx={{color: "#999797"}}>
            GEF-DAOの仮想表からGEF-JDBCのAPIコードに変換するためのページです。
          </Typography>
        </Box>
        <Box flexGrow={1} textAlign={"right"} alignContent={"right"} paddingRight={"25px"}>
          <Button variant='outlined' onClick={handleClickOpenPreview}>変換の実行</Button>
        </Box>
      </Box>
     
      <Box display="flex" flexDirection="column" margin="auto">
        <TabContext value={tabIndex}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
        <TabList onChange={handleTabChange} aria-label="lab API tabs example">
          <Tab label="変換設定の選択" value="1" />
          <Tab label="変換条件の管理" value="2" />
        </TabList>
        </Box>
        <TabPanel value="1">
          <Typography fontWeight={"bold"} textAlign={"left"}>
            変換設定の選択タブ
          </Typography>
          <Typography textAlign={"left"} fontSize={16} sx={{color: "#999797"}}marginBottom={2}>
            このタブは「変換設定管理」ページに登録された「変換設定」を選択してください。選択された変換設定は変換を実行する時に使用されます。
          </Typography>
          <Box textAlign="left" paddingTop="10px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5, marginBottom: 2}}>
              <InputLabel required>変換設定: </InputLabel>
              <Select
                required
                size="small"
                data-testid="global-setting-select"
                displayEmpty
                value={tempGlobalSetting ? tempGlobalSetting.id : ""}
                onChange={onChangeGlobalSetting}
                fullWidth={true}
              >
                <MenuItem disabled value="">
                  <em>設定を選んでください</em>
                </MenuItem>
                {settings.map((item: Setting) => (
                  <MenuItem key={item.id} value={item.id}>
                    {item.savedName}
                  </MenuItem>
                ))}
              </Select>
              <Box>
                <SimpleTreeView defaultExpandedItems={['grid']}>
                  <CustomTreeItem itemId="grid" label="詳細">
                    <SimpleTreeView defaultExpandedItems={['grid']}>
                      <CustomTreeItem itemId="db" label="データベース設定">
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>URL：{tempGlobalSetting ? tempGlobalSetting.db.url : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>スキーマ：{tempGlobalSetting ? tempGlobalSetting.db.schema : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>データベースタイプ：{tempGlobalSetting ? tempGlobalSetting.db.dbType : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>ユーザ名：{tempGlobalSetting ? tempGlobalSetting.db.username : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>パスワード：{tempGlobalSetting ? tempGlobalSetting.db.password : "None"}</Typography>
                        </Box>
                      </CustomTreeItem>
                    </SimpleTreeView>
                    
                    <SimpleTreeView defaultExpandedItems={['grid']}>
                      <CustomTreeItem itemId="locale" label="言語設定">
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>使用する言語：{tempGlobalSetting ? tempGlobalSetting.locale.current : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>country1の言語：{tempGlobalSetting ? tempGlobalSetting.locale.mapping.country1 : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>country2の言語：{tempGlobalSetting ? tempGlobalSetting.locale.mapping.country2 : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>country3の言語：{tempGlobalSetting ? tempGlobalSetting.locale.mapping.country3 : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>country4の言語：{tempGlobalSetting ? tempGlobalSetting.locale.mapping.country4 : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>country5の言語：{tempGlobalSetting ? tempGlobalSetting.locale.mapping.country5 : "None"}</Typography>
                        </Box>
                      </CustomTreeItem>
                    </SimpleTreeView>
                    
                    <SimpleTreeView defaultExpandedItems={['grid']}>
                      <CustomTreeItem itemId="reservedWords" label="予約語設定">
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>Primaryキーカラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.primaryKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>Exclusiveキーカラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.exclusiveKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>会社コードカラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.companyCodeKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>登録者IDカラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.insertedUserIDKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>登録日付カラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.insertedDateKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>更新者IDカラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.updatedUserIDKey : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>更新日付カラム：{tempGlobalSetting ? tempGlobalSetting.reservedWords.updatedDateKey : "None"}</Typography>
                        </Box>
                      </CustomTreeItem>
                    </SimpleTreeView>

                    <SimpleTreeView defaultExpandedItems={['grid']}>
                      <CustomTreeItem itemId="transform" label="アウトプット設定">
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>ダウンロードされたファイルのエンコーディング：{tempGlobalSetting ? tempGlobalSetting.transform.encoding : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>SQL処理にエンティティ定義情報を使用する：{tempGlobalSetting ? tempGlobalSetting.transform.entityQuery ? "true" : "false" : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>項目のエイリアスを仮想項目名に上書きする：{tempGlobalSetting ? tempGlobalSetting.transform.forceAliasColumn ? "true" : "false" : "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>引数の値をバインドする際に配列を使用するかどうか：{tempGlobalSetting ? tempGlobalSetting.transform.useForeignKey ? "true" : "false": "None"}</Typography>
                        </Box>
                        <Box display={"flex"}>
                          <Typography sx={{fontSize: "10pt"}}>エンティティ定義の外部キーを使用する：{tempGlobalSetting ? tempGlobalSetting.transform.useExpMap ? "true" : "false" : "None"}</Typography>
                        </Box>
                      </CustomTreeItem>
                    </SimpleTreeView>
                  
                  </CustomTreeItem>
                </SimpleTreeView>
              </Box>
          </Box>
        </TabPanel>
        <TabPanel value="2">
          <Box display={"flex"} flexGrow={1}>
            <Box>
              <Typography fontWeight={"bold"} textAlign={"left"}>
                変換条件の管理タブ
              </Typography>
              <Typography textAlign={"left"} fontSize={16} sx={{color: "#999797"}}marginBottom={2}>
                このタブは変換するための条件を管理します。変換条件の中に仮想表IDや他の条件などを設定することができます。
              </Typography>
            </Box>
            <Box textAlign="right" flexGrow={1} paddingBottom="10px" >
              <Button variant='outlined' onClick={addNewScript}>変換条件の新規作成</Button>
            </Box>
          </Box>
          <input
            ref={fileInputRef}
            type="file"
            test-id="fileInputSettingList"
            accept=".yaml, .yml" 
            style={{ display: 'none' }} 
            onChange={importInputs}
          />
          <Box textAlign="left" paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 1, borderRadius: 5, marginBottom: 2}}>
            <Box display="flex">
              <Typography variant="h6" fontWeight={"bold"} textAlign={"left"} marginBottom={1}>仮想表転換の条件一覧</Typography>
              <Box alignContent="right" flexGrow={1} justifyContent={"flex-end"} textAlign="right" marginBottom={2}><Button variant="outlined" onClick={handleClickMenu} endIcon={<KeyboardArrowDownIcon />}>アクション</Button></Box>
                <Menu
                  id="demo-positioned-menu"
                  aria-labelledby="demo-positioned-button"
                  anchorEl={anchorElMenu}
                  open={menuOpen}
                  onClose={handleCloseMenu}
                  anchorOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                  }}
                  transformOrigin={{
                    vertical: 'top',
                    horizontal: 'left',
                  }}
                >
                  <MenuItem onClick={()=> {
                    handleImportFile();
                    handleCloseMenu();
                  }}>
                 インポート（コマンドラインのyamlファイル）</MenuItem>
                  <MenuItem onClick={()=> {
                    deleteSubjectScripts();
                    handleCloseMenu();
                  }} disabled={isSelectedScriptExist()}>削除</MenuItem>
                </Menu>
              </Box>
            <Grid2 container spacing={2}>
              <Grid2 size={8}>
                <TextField fullWidth label="IDで検索する" id="searchBox" size="small" value={searchTerm} onChange={e=> {
                    setSearchTerm(e.target.value)}
                }/>
              </Grid2>
              <Grid2 size={4}>
                <Select
                      required
                      displayEmpty
                      size="small"
                      value={crudTypeTerm}
                      onChange={(e)=> {
                        setCrudTypeTerm(e.target.value);
                      }}
                      data-testid="crud-type-select"
                      fullWidth={true}
                  >
                      <MenuItem value="">
                          <em>CRUDタイプを選んでください</em>
                      </MenuItem>
                      <MenuItem value="SELECT">
                          <em>SELECT</em>
                      </MenuItem>
                      <MenuItem value="UPDATE">
                          <em>UPDATE</em>
                      </MenuItem>
                      <MenuItem value="DELETE">
                          <em>DELETE</em>
                      </MenuItem>
                      <MenuItem value="INSERT">
                          <em>INSERT</em>
                      </MenuItem>
                  </Select>
              </Grid2>
            </Grid2>
            {filteredScripts.length == 0 ? (
              <Box textAlign="center" fontSize="15pt" data-testid="empty-input-image"> 
                  <img src="/empty-box.png" width="200px"></img>
                  <Typography variant='h6' fontWeight="bold">変換条件は空です</Typography>
              </Box>
            ) : 
            <>
              <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
                {filteredScripts.map((item: Script, index: number) => (
                    <ListItem
                      key={item.description}
                      disablePadding
                    >
                      <Checkbox
                        edge="start"
                        checked={scriptsChecked[item.id]}
                        tabIndex={-1}
                        data-testid={"script-checkbox-"+index}
                        disableRipple
                        onChange={(e)=> {
                          scriptsChecked[item.id] = e.target.checked;
                          setScriptsChecked({...scriptsChecked});
                        }}
                        inputProps={{ 'aria-labelledby': item.id }}
                      />
                      <ListItemButton data-testid={"script-item-"+index} role={undefined} onClick={()=>openScriptDetails(item.id)} dense>
                          
                        {/* <ListItemAvatar>
                            <Container sx={{backgroundColor: "black", margin: "5px", padding: "5px", fontWeight: "bold", borderRadius: "100px", marginRight: "10px", marginLeft: "-10px"}}>
                              <Box sx={{backgroundColor: "white", color: "black", borderRadius: "100px", width: "20px", height: "20px", textAlign: "center"}}>{item.crudType.charAt(0)}</Box>
                            </Container>
                        </ListItemAvatar> */}
                        <ListItemText primary={<React.Fragment>
                          <Typography fontWeight="bold" fontSize={"14px"}>{item.description}</Typography>
                          </React.Fragment>
                          } secondary={
                            <React.Fragment>
                              <Typography fontSize={"12px"}>{
                                item.type
                              }</Typography>
                              <Box display={"flex"} gap={1}>
                                {item.crudTypeSelect ? <Box fontSize={"12px"}sx={{
                                  backgroundColor: "#1b85d1",
                                  color: "white",
                                  padding: "2px 5px",
                                  fontWeight: "bold",
                                  borderRadius: "5px",
                                  marginRight: "0.5px",
                                  fontSize: "10px"
                                }}>SELECT</Box> : ""}
                                {item.crudTypeUpdate ? <Box fontSize={"12px"}sx={{
                                  backgroundColor: "#1b85d1",
                                  color: "white",
                                  padding: "2px 5px",
                                  fontWeight: "bold",
                                  marginRight: "0.5px",
                                  borderRadius: "5px",
                                  fontSize: "10px"
                                }}>UPDATE</Box> : ""}
                                {item.crudTypeDelete ? <Box fontSize={"12px"}sx={{
                                  backgroundColor: "#1b85d1",
                                  color: "white",
                                  padding: "2px 5px",
                                  fontWeight: "bold",
                                  marginRight: "0.5px",
                                  borderRadius: "5px",
                                  fontSize: "10px"
                                }}>DELETE</Box> : ""}
                                {item.crudTypeInsert ? <Box fontSize={"12px"}sx={{
                                  backgroundColor: "#1b85d1",
                                  color: "white",
                                  padding: "2px 5px",
                                  fontWeight: "bold",
                                  marginRight: "0.5px",
                                  borderRadius: "5px",
                                  fontSize: "10px"
                                }}>INSERT</Box> : ""}
                              </Box>
                            
                          </React.Fragment>
                          }
                        />
                      </ListItemButton>
                    </ListItem>
                  ))}
                
                </List>
            </>
            }
        </Box>
        </TabPanel>
      </TabContext>
    </Box>
      <InputDialog open={open} initialScript={script} onSubmit={addItem} onClose={handleClose}/>

      <Dialog
        fullScreen
        open={openPreview}
        onClose={handleClickClosePreview}
      >
        <Box sx={{ position: 'relative', backgroundColor: "white", color: "#1b85d1", borderBottom: "solid", borderBottomWidth: "0.5px", borderBottomColor: "black"}}>
          <Toolbar>
              <IconButton
                edge="start"
                color="inherit"
                onClick={handleClickClosePreview}
                aria-label="close"
              >
                <Close />
              </IconButton>
              <Typography sx={{ ml: 2, flex: 1 }} variant="h6" fontWeight="bold" component="div">
              処理画面
              </Typography>
          </Toolbar>
        </Box>
        <ConfirmationPage scripts={scripts}/>
      </Dialog>
      <div id="loadingOverlay2" className="overlay">
          <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%'}}>
              <div className="spinner"></div>
              <div style={{ paddingLeft: '20px', paddingTop: '20px' }}>確認中...</div>
          </div>
      </div>
    </Box>
  );
}
export default InputListPage;