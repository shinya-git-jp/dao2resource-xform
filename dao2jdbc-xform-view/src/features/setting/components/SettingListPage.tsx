import * as React from 'react';
import Box from '@mui/material/Box';
import { Button, Checkbox, Container, List, ListItem, ListItemButton, ListItemText, Menu, MenuItem, TextField, Typography } from '@mui/material';
import {SettingDialog} from './SettingDialog';
import { exportFile, readYaml, saveSettingsToBrowser, SettingsSchema } from '../../../utils/utils';
import { useSettingListPageData } from '../hooks/useSettingListPageData';
import { DbType } from '../../../types/enum';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import { v4 as uuidv4 } from 'uuid';
import { RequestManager } from '../../../utils/RequestManager';

export default function SettingListPage() {
  const {
    openDialog,
    setOpenDialog,
    settingsChecked,
    setSettingsChecked,
    settings,
    setSettings,
    removeSetting,
    fileInputRef,
    searchTerm,
    setSearchTerm,
    addSetting,
    setSetting,
    setting
  } = useSettingListPageData();
  const handleClickOpenDialog = () => {
    setOpenDialog(true);
  };
  const handleClickCloseDialog = () => {
    setOpenDialog(false);
    RequestManager.cancelAll();
  }
  const openSettingDetail = (id: string) => {
    const setting = settings.find((setting: Setting) => setting.id == id);
    if (setting) {
      setSetting(setting);
      handleClickOpenDialog();
    }
  }
  const addNewSetting = () => {
    reset();
    handleClickOpenDialog();
  }
  const exportSettings = () => {
    let userInput = prompt("ファイル名を入力してください(拡張子を含まない):（デフォルト：settings）");

    if (userInput !== null && userInput.trim() !== "") {
        userInput = userInput.trim();
    } else {
        userInput = "settings";
    }
    exportFile(getCheckedSettings(), userInput);
  }
  const handleImportFile = () => {
    fileInputRef.current.click();
  };
  const parsedData = (data: Array<Setting>) => {
    try {
      SettingsSchema.parse(data);
      setSettings(data);
      saveSettingsToBrowser(data);
      alert("インポートが成功しました。");
    } catch(e) {
      console.error("Invalid format:", e);
      alert("フォーマットは違います。");
    }
    
  }
  const importSettings = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files && event.target.files[0];
    if (file) {
      readYaml(file, parsedData);
    }
  };
  const getCheckedSettings = () => {
    const checkedSettings = settings.filter((setting: {id: string}) => settingsChecked[setting.id]);
    return checkedSettings;
  }
  const isSelectedSettingsExist = () : boolean => {
    return Object.values(settingsChecked).every(element => !element);
  }
  const deleteSubjectSettings = () => {
    const userConfirmed = window.confirm("Are you sure you want to delete?");
    if (userConfirmed) {
      getCheckedSettings().forEach((setting)=> {
        if (settingsChecked[setting.id]) {
          removeSetting(setting.id);
        }
      })
      setSettingsChecked([])
    }
  }
  const filteredSettings = settings.filter((setting: Setting) => {
      const searchValue = searchTerm.toLowerCase();
      const isIncluded = setting.savedName.toLowerCase().includes(searchValue);
      if (!isIncluded) {
        settingsChecked[setting.id] = false;
      }
      return isIncluded;
    }
  );

  const updateSetting = (setting: Setting)=> {
    const tempSetting = settings.find((s: {id: string}) => s.id === setting.id);
    if (tempSetting) {
      tempSetting.db = setting.db;
      tempSetting.reservedWords = setting.reservedWords;
      tempSetting.locale = setting.locale;
      tempSetting.savedName = setting.savedName;
      tempSetting.transform.encoding = setting.transform.encoding;
      tempSetting.transform.forceAliasColumn = setting.transform.forceAliasColumn;
      tempSetting.transform.useForeignKey = setting.transform.useForeignKey;
      tempSetting.transform.useExpMap = setting.transform.useExpMap;
      tempSetting.transform.entityQuery = setting.transform.entityQuery;
    }
  }
  const reset = () => {
    setSetting({
        id: uuidv4(),
        savedName: "",
        db: {
          url: "",
          dbType: DbType.ORACLE,
          schema: "",
          username: "",
          password: ""
        },
        locale: {
        current: "ja",
        mapping: {
            country1: "",
            country2: "",
            country3: "",
            country4: "",
            country5: ""
        }
        },
        reservedWords: {
          primaryKey: "",
          exclusiveKey: "",
          companyCodeKey: "",
          insertedUserIDKey: "",
          insertedDateKey: "",
          updatedUserIDKey: "",
          updatedDateKey: ""
        },
        transform: {
          encoding: "UTF-8",
          forceAliasColumn: false,
          entityQuery: true,
          useForeignKey: false,
          useExpMap: false
        },
        savedTime: ""
    })
  };
  
  const saveSetting = (setting: Setting)=> {
    if (settings.find((s: {id: string}) => s.id === setting.id)) {
      updateSetting(setting);
    } else {
      addSetting(setting);
    }
    saveSettingsToBrowser(settings);
    alert("保存しました。");
    reset();
  }
  
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };
  
  return (
    <Box>
        <Box sx={{marginBottom: "5px"}}>
          <Box display="flex">
            <Box sx={{marginBottom: "10px", textAlign: "left"}}>
              <Typography variant="h6" textTransform="uppercase" fontWeight="bold" >変換設定の管理ページ</Typography>
              <Typography gutterBottom textAlign={"left"} fontSize={16} sx={{color: "#999797"}}>
                変換を実行するための設定を管理するためのページです。
              </Typography>
            </Box>
              <Box flexGrow={1} textAlign={"right"}>
                <Box textAlign="right" marginBottom={2}><Button variant="outlined"  onClick={addNewSetting}>変換設定の新規作成</Button></Box>
              </Box>
          </Box>
        </Box>
        <input
          ref={fileInputRef}
          type="file"
          test-id="fileInputSettingList"
          accept=".yaml, .yml" 
          style={{ display: 'none' }} 
          onChange={importSettings}
        />
        <Box textAlign="center" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 1, borderRadius: 5}}>
            <Box display="flex">
              <Typography variant='h6' fontWeight={"bold"} textAlign={"left"} marginBottom={1}>変換設定の一覧</Typography>
              <Box alignContent="right" flexGrow={1} justifyContent={"flex-end"} textAlign="right" marginBottom={2}><Button variant="outlined" onClick={handleClick} endIcon={<KeyboardArrowDownIcon />}>アクション</Button></Box>
              <Menu
                id="demo-positioned-menu"
                aria-labelledby="demo-positioned-button"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
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
                  handleClose();
                }}>
                インポート</MenuItem>
                <MenuItem onClick={()=> {
                  exportSettings();
                  handleClose();
                }} disabled={isSelectedSettingsExist()}>エクスポート</MenuItem>
                <MenuItem onClick={()=> {
                  deleteSubjectSettings();
                  handleClose();
                }} disabled={isSelectedSettingsExist()}>削除</MenuItem>
              </Menu>
            </Box>
           <TextField fullWidth label="保存名で検索する" id="settingSearchField" size="small" value={searchTerm} onChange={e=> {
              setSearchTerm(e.target.value)}
            }/>
            {filteredSettings.length == 0 ? (
                    <Box textAlign="center" fontSize="15pt" data-testid="empty-setting-image"> 
                        <img src="/empty-box.png" width="200px"></img>
                        <Typography variant='h6' fontWeight="bold">アイテムは空です</Typography>
                    </Box>
              ) : 
            <List sx={{ width: '100%', bgcolor: 'background.paper' }}>
                {filteredSettings.map((setting, index) => {
                    return (
                      <ListItem
                        key={setting.id}
                        disablePadding
                      >
                         <Checkbox
                          edge="start"
                          data-testid={"setting-checkbox-"+index}
                          checked={settingsChecked[setting.id]}
                          tabIndex={-1}
                          disableRipple
                          onChange={(e)=> {
                            settingsChecked[setting.id] = e.target.checked;
                            setSettingsChecked({...settingsChecked});
                          }}
                          inputProps={{ 'aria-labelledby': setting.id }}
                        />
                        
                        <ListItemButton data-testid={"setting-item-"+index} role={undefined} onClick={()=>openSettingDetail(setting.id)} dense>
                          <ListItemText 
                            primary={<React.Fragment>
                              <Typography fontWeight="bold" fontSize={"14px"}>{setting.savedName}</Typography>
                            </React.Fragment>} secondary={setting.savedTime} />
                        </ListItemButton>
                      </ListItem>
                    );
                })}
                </List>
            }
        </Box>
        <SettingDialog open={openDialog} initialSetting={setting} onSubmit={saveSetting} onClose={handleClickCloseDialog}/>
    </Box>
    
  );
}