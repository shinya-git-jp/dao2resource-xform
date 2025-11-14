import * as React from 'react';
import Box from '@mui/material/Box';
import { Button, Checkbox, Dialog, FormControlLabel, IconButton, InputAdornment, InputLabel, MenuItem, Select, TextField, Toolbar, Tooltip, Typography } from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';
import { readYaml, SettingSchemaInput, TitleTreeItem } from '../../../utils/utils';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import useSettingDialogData from '../hooks/useSettingDialogData';
import { DbType } from '../../../types/enum';
import { Close, Visibility, VisibilityOff } from '@mui/icons-material';
import { checkSettingConnection } from '../../../services/setting-service';
import Tab from '@mui/material/Tab';
import TabContext from '@mui/lab/TabContext';
import TabList from '@mui/lab/TabList';
import TabPanel from '@mui/lab/TabPanel';
import { boolean, object, string } from 'zod';
import CustomLabel from '../../../components/CustomLabel';

interface SettingDialogProps {
  open: boolean;
  initialSetting: Setting;
  onSubmit: (setting: Setting) => void;
  onClose: () => void;
}

export const SettingDialog : React.FC<SettingDialogProps>  = ({open, initialSetting, onSubmit, onClose}) => {
  const {
    setting,
    setSavedName,
    setUrl,
    setDbType,
    setSchema,
    setUsername,
    setPassword,
    setCurrentLocale,
    setCountry1,
    setCountry2,
    setCountry3,
    setCountry4,
    setCountry5,
    setPrimaryKey,
    setExclusiveKey,
    setCompanyCodeKey,
    setInsertedUserIDKey,
    setInsertedDateKey,
    setUpdatedUserIDKey,
    setUpdatedDateKey,
    setTransformEncoding,
    setForceAliasColumn,
    setUseForeignKey,
    setUseExpMap,
    setEntityQuery,
    setSetting,
    showPassword,
    setShowPassword,
    handleClickShowPassword,
  } = useSettingDialogData(initialSetting);
  const fileInputRef = React.useRef(null);
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
  const validateDb = async () => {
      const dbSchema = object({
        url: string().min(1, "「URL」は必須項目です。"),
        schema: string().min(1, "「スキーマ」は必須項目です。"),
        dbType: string().min(1, "「DBタイプ」は必須項目です。"),
        username: string().min(1, "「ユーザ名」は必須項目です。"),
        password: string().min(1, "「パスワード」は必須項目です。")
      });
      return await dbSchema.safeParse(setting.db);
  }
  const checkConnection = async () => {
    const validatedResult = await validateDb();
    if (!validatedResult.success) {
      console.log(validatedResult.error.errors[0].path);
      alert(validatedResult.error.errors[0].message);
      return;
    } 
    showLoading();
      try {
        const isSuccess = await checkSettingConnection(setting);
        if (isSuccess) {
          hideLoading();
          alert("接続成功！");
          return;
        }
        alert("接続エラー");
      } catch (error) {
        console.error("Error:", error);
        alert("接続エラー");
      }
      
      hideLoading();
  }
  const parsedData = (data: any) => {
    try {
      SettingSchemaInput.parse(data);
      setSetting({
        ...setting,
            db: {
              url: data.db.url,
              schema: data.db.schema,
              dbType: data.db.dbType,
              username: data.db.username,
              password: data.db.password
            },
            reservedWords: {
              primaryKey: data.reservedWords.primaryKey,
              exclusiveKey: data.reservedWords.exclusiveKey,
              companyCodeKey: data.reservedWords.companyCodeKey,
              insertedUserIDKey: data.reservedWords.insertedUserIDKey,
              insertedDateKey: data.reservedWords.insertedDateKey,
              updatedUserIDKey: data.reservedWords.updatedUserIDKey,
              updatedDateKey: data.reservedWords.updatedDateKey
            },
            locale: {
              current: data.locale.current,
              mapping: data.locale.mapping
            },
            transform: {
              encoding: data.transform.encoding ? data.transform.encoding : "UTF-8",
              forceAliasColumn: data.transform.forceAliasColumn,
              entityQuery: data.transform.entityQuery,
              useForeignKey: data.transform.useForeignKey,
              useExpMap: data.transform.useExpMap
            }
      });
      alert("インポートが成功しました。");
    } catch(exception) {
      alert("フォーマットは違います");
    }
   
  }
  const handleImportFile = () => {
    fileInputRef.current.click();
  };
  const handleFileChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      readYaml(file, parsedData);
    }
  };
  const [tabIndex, setTabIndex] = React.useState('1');
  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setTabIndex(newValue);
  };
  const [errorField, setErrorField] = React.useState<string>("");
  const validateForm = async (setting: Setting) => {
    let settingSchema = object({
      id: string().min(1, "「ID」は必須項目です。"),
      savedName: string().min(1, "「保存名」は必須項目です。"),
      db: object({
        url: string().min(1, "「URL」は必須項目です。"),
        schema: string().min(1, "「スキーマ」は必須項目です。"),
        dbType: string().min(1, "「DBタイプ」は必須項目です。"),
        username: string().min(1, "「ユーザ名」は必須項目です。"),
        password: string().min(1, "「パスワード」は必須項目です。")
      }).required(),
      locale: object({
        current: string().min(1, "「使用する言語」は必須項目です。"),
        mapping: object({
          country1: string().min(1, "「country1の言語」は必須項目です。"),
          country2: string().nullable().optional(),
          country3: string().nullable().optional(),
          country4: string().nullable().optional(),
          country5: string().nullable().optional()
        })
      }),
      reservedWords: object({
        primaryKey: string().min(1, "「Primaryキーカラム」は必須項目です。"),
        exclusiveKey: string().min(1, "「Exclusiveキーカラム」は必須項目です。"),
        companyCodeKey: string().min(1, "「会社コードカラム」は必須項目です。"),
        insertedUserIDKey: string().min(1, "「登録者IDカラム」は必須項目です。"),
        insertedDateKey: string().min(1, "「登録者日付カラム」は必須項目です。"),
        updatedUserIDKey: string().min(1, "「更新者IDカラム」は必須項目です。"),
        updatedDateKey: string().min(1, "「更新日付カラム」は必須項目です。")
      }),
      transform: object({
        encoding: string().default("UTF-8"),
        forceAliasColumn: boolean().default(false),
        entityQuery: boolean().default(true),
        useForeignKey: boolean().default(false),
        useExpMap: boolean().default(false)
      }),
      savedTime: string().default(new Date().toLocaleString())
    });

    // parse and assert validity
    return await settingSchema.safeParse(setting);
  }
  const requiredRefs = {
    db: {
      url: React.useRef<HTMLInputElement>(null),
      schema: React.useRef<HTMLInputElement>(null),
      dbType: React.useRef<HTMLInputElement>(null),
      username: React.useRef<HTMLInputElement>(null),
      password: React.useRef<HTMLInputElement>(null),
    },
    reservedWords: {
      primaryKey: React.useRef<HTMLInputElement>(null),
      exclusiveKey: React.useRef<HTMLInputElement>(null),
      companyCodeKey: React.useRef<HTMLInputElement>(null),
      insertedUserIDKey: React.useRef<HTMLInputElement>(null),
      insertedDateKey: React.useRef<HTMLInputElement>(null),
      updatedUserIDKey: React.useRef<HTMLInputElement>(null),
      updatedDateKey: React.useRef<HTMLInputElement>(null),
    },
    locale: {
      current: React.useRef<HTMLInputElement>(null),
      mapping: {
        country1: React.useRef<HTMLInputElement>(null),
      }
    }
  };
  const onCloseDialog = ()=> {
    setErrorField("");
    onClose();
  }
  return (
    <Dialog
      fullScreen
      open={open}
      onClose={onCloseDialog}
    >
      <Box sx={{ position: 'relative', backgroundColor: "white", color: "#1b85d1", borderBottom: "solid", borderBottomWidth: "0.5px", borderBottomColor: "black"}}>
        <Toolbar>
            <IconButton
              edge="start"
              color="inherit"
              onClick={onCloseDialog}
              aria-label="close"
            >
              <Close />
            </IconButton>
            <Typography sx={{ ml: 2, flex: 1 }} variant="h6" fontWeight="bold" component="div">
              変換設定の新規作成
            </Typography>
          </Toolbar>
        </Box>
        <Box sx={{padding: 5}}>
      <Box textAlign="center" paddingTop="5px" component={"form"} onSubmit={async (e) => {
        e.preventDefault();
        const name = prompt("変換設定の保存名を入力してください:", setting.savedName);
        if (name === null) {
          return;
        }
        const validatedResult = await validateForm({...setting, savedName: name});
        if (!validatedResult.success) {
          const errorPath = validatedResult.error.errors[0].path;
          setErrorField(errorPath.join('.'));
          let ref = requiredRefs;
          let tabToSet = tabIndex;
          if (errorPath[0] === "db") tabToSet = "1";
          else if (errorPath[0] === "locale") tabToSet = "2";
          else if (errorPath[0] === "reservedWords") tabToSet = "3";
          else if (errorPath[0] === "transform") tabToSet = "4";

          for (const key of errorPath) {
            if (ref[key]) ref = ref[key];
          }
          alert(validatedResult.error.errors[0].message);
          
          if (tabToSet !== tabIndex) {
            setTabIndex(tabToSet);
            setTimeout(() => {
              if (ref && 'current' in ref && ref.current) {
                (ref.current as HTMLInputElement).focus();
              }
            }, 100); 
          } else {
             if (ref && 'current' in ref && ref.current) {
              (ref.current as HTMLInputElement).focus();
            }
          }
         
          return;
      } else {
        setErrorField("");
      } 
        const validatedSetting = validatedResult.data;
        if (name !== null) {
          onSubmit({
              id: setting.id,
              savedName: name,
              savedTime: new Date().toLocaleString(),
              db: {
                url: validatedSetting.db.url,
                schema: validatedSetting.db.schema,
                dbType: validatedSetting.db.dbType,
                username: validatedSetting.db.username,
                password: validatedSetting.db.password
              },
              reservedWords: {
                primaryKey: validatedSetting.reservedWords.primaryKey,
                exclusiveKey: validatedSetting.reservedWords.exclusiveKey,
                companyCodeKey: setting.reservedWords.companyCodeKey,
                insertedUserIDKey: setting.reservedWords.insertedUserIDKey,
                insertedDateKey: setting.reservedWords.insertedDateKey,
                updatedUserIDKey: setting.reservedWords.updatedUserIDKey,
                updatedDateKey: setting.reservedWords.updatedDateKey
              },
              locale: {
                current: setting.locale.current,
                mapping: setting.locale.mapping
              },
              transform: {
                encoding: setting.transform.encoding ? setting.transform.encoding : "UTF-8",
                forceAliasColumn: setting.transform.forceAliasColumn,
                entityQuery: setting.transform.entityQuery,
                useForeignKey: setting.transform.useForeignKey,
                useExpMap: setting.transform.useExpMap
              }
          });
          onClose();
        } 
      }}>
        
        <Box display="flex" flexDirection="row-reverse" gap="5px" textAlign={"right"} marginBottom={"10px"}>
          <Button variant='outlined' type='submit'>保存</Button>
          <input
              ref={fileInputRef}
              type="file"
              test-id="fileInputSettingDialog"
              accept=".yaml, .yml" 
              style={{ display: 'none' }} 
              onChange={handleFileChange}
            />
            <Button variant='outlined' onClick={handleImportFile}>インポート(コマンドライン版のyamlファイル)</Button>
        </Box>  
        <Box display="flex" flexDirection="column">
            {/* <Box marginTop="30px" marginBottom="15px" textAlign="start">
              <div style={{display: "flex", textAlign: "center"}}>
                <InputLabel required>設定名: </InputLabel>
                <Tooltip title="識別するための名前になります。" arrow>
                  <InfoIcon fontSize='small'></InfoIcon>
                </Tooltip>
              </div>
              <TextField
                required
                placeholder='設定名'
                fullWidth={true}
                size="small"
                value={setting.savedName}
                onChange={(e) => {setSavedName(e.target.value)}}
              />
            </Box> */}
          <Box paddingTop="50px" textAlign={"left"} sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5}}>
            <TabContext value={tabIndex}>
              <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <TabList onChange={handleChange} aria-label="lab API tabs example">
                  <Tab label="データベース設定" value="1" />
                  <Tab label="言語設定" value="2" />
                  <Tab label="予約語設定" value="3" />
                  <Tab label="出力設定" value="4" />
                </TabList>
              </Box>
              <TabPanel value="1">
                <Typography textAlign={"left"} fontSize={14} sx={{color: "#999797"}}marginBottom={2}>
                  このタブは変換時に使用されるデータベースの設定を入力してください。
                  仮想表とエンティティは設定されたデータベースから取得されます。
                </Typography>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"URL: "} tooltipText={"接続先のデータベースURLです。"}/>
                  <TextField
                    placeholder='URL'
                    fullWidth={true}
                    size="small"
                    value={setting.db.url}
                    inputRef={requiredRefs.db.url}
                    error={errorField === 'db.url'}
                    onChange={(e) => {setUrl(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"データベースタイプ: "} tooltipText={"接続先のデータベースタイプです。"}/>
                  <Select
                      size="small"
                      displayEmpty
                      data-testid="selected-setting"
                      value={setting.db.dbType}
                      error={errorField === 'db.dbType'}
                      fullWidth={true}
                      onChange={(e) => {
                          let dbType = e.target.value;
                          if (e.target.value == "") {
                            dbType = ""
                          }
                          setDbType(dbType)
                      }}
                    inputRef={requiredRefs.db.dbType}
                  >
                      <MenuItem  value="">
                          <em>データベースタイプを選んでください</em>
                      </MenuItem>
                      <MenuItem key="oracle" value={DbType.ORACLE}>
                          Oracle
                      </MenuItem>
                      <MenuItem key="mysql" value={DbType.MYSQL}>
                          MySQL
                      </MenuItem>
                      <MenuItem key="db2" value={DbType.DB2}>
                          DB2
                      </MenuItem>
                  </Select>
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"スキーマ: "} tooltipText={"接続先のデータベースのスキーマです。"}/>
                  <TextField
                    placeholder='Scheme'
                    fullWidth={true}
                    size="small"
                    value={setting.db.schema}
                    error={errorField === 'db.schema'}
                    onChange={(e) => {setSchema(e.target.value)}}
                    inputRef={requiredRefs.db.schema}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"ユーザ名: "} tooltipText={"接続先のデータベースのユーザ名です。"}/>
                  
                  <TextField
                    placeholder='Username'
                    fullWidth={true}
                    size="small"
                    value={setting.db.username}
                    error={errorField === 'db.username'}
                    onChange={(e) => {setUsername(e.target.value)}}
                    inputRef={requiredRefs.db.username}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"パスワード: "} tooltipText={"接続先のデータベースのパスワードです。"}/>
                  <TextField
                      type={showPassword ? 'text' : 'password'}
                      placeholder='Password'
                      fullWidth={true}
                      size="small"
                      value={setting.db.password}
                      error={errorField === 'db.password'}
                      onChange={(e) => {setPassword(e.target.value)}}
                      inputRef={requiredRefs.db.password}
                      InputProps={{
                        endAdornment: (
                          <InputAdornment position="end">
                            <IconButton
                              aria-label="toggle password visibility"
                              onClick={handleClickShowPassword}
                              edge="end"
                            >
                              {showPassword ? <VisibilityOff /> : <Visibility />}
                            </IconButton>
                          </InputAdornment>
                        ),
                      }}
                    />
                  <Box marginTop="20px">
                    <Button variant='outlined' onClick={checkConnection}>接続テスト</Button>
                  </Box>
                </Box>
              </TabPanel>
              <TabPanel value="2">
                <Typography textAlign={"left"} fontSize={14} sx={{color: "#999797"}}marginBottom={2}>
                  このタブはGEF-DAOの「geframe.jk2.Locale.Language1」～「geframe.jk2.Locale.Language5」の情報を入力してください。
                  言語情報は出力されたユーニックキー名や外部キー名などの言語になります。
                </Typography>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"使用する言語: "} tooltipText={"変換する時に使用される言語です。ツール利用時に何の言語で出力するのかを設定します。localeはSLocalizationにあるリソース名に依存するため、ユニックキー名や結合キー名などはlocaleの値次第になります。"}/>
                  <TextField
                    placeholder='使用する言語'
                    fullWidth={true}
                    size="small"
                    value={setting.locale.current}
                    error={errorField === 'locale.current'}
                    inputRef={requiredRefs.locale.current}
                    onChange={(e) => {setCurrentLocale(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"country1の言語: "} tooltipText={"gef-daoプロパティにある「geframe.jk2.Locale.Language1」を指しています。"}/>
                  
                  <TextField
                    placeholder='country1の言語'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'locale.mapping.country1'}
                    value={setting.locale.mapping.country1}
                    inputRef={requiredRefs.locale.mapping.country1}
                    onChange={(e) => {setCountry1(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"country2の言語: "} tooltipText={"gef-daoプロパティにある「geframe.jk2.Locale.Language2」を指しています。"}/>
                  
                  <TextField
                    placeholder='country2の言語'
                    fullWidth={true}
                    size="small"
                    value={setting.locale.mapping.country2}
                    onChange={(e) => {setCountry2(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"country3の言語: "} tooltipText={"gef-daoプロパティにある「geframe.jk2.Locale.Language3」を指しています。"}/>
                  
                  <TextField
                    placeholder='country3の言語'
                    fullWidth={true}
                    size="small"
                    value={setting.locale.mapping.country3}
                    onChange={(e) => {setCountry3(e.target.value)}}
                  />
                </Box>
                
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"country4の言語: "} tooltipText={"gef-daoプロパティにある「geframe.jk2.Locale.Language4」を指しています。"}/>
                  
                  <TextField
                    placeholder='country4の言語'
                    fullWidth={true}
                    size="small"
                    value={setting.locale.mapping.country4}
                    onChange={(e) => {setCountry4(e.target.value)}}
                  />
                </Box>
                
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"country5の言語: "} tooltipText={"gef-daoプロパティにある「geframe.jk2.Locale.Language5」を指しています。"}/>
                  
                  <TextField
                    placeholder='country5の言語'
                    fullWidth={true}
                    size="small"
                    value={setting.locale.mapping.country5}
                    onChange={(e) => {setCountry5(e.target.value)}}
                  />
                </Box>
              </TabPanel>
              <TabPanel value="3">
                <Typography textAlign={"left"} fontSize={14} sx={{color: "#999797"}}marginBottom={2}>
                  このタブはGEF-DAOのプロパティファイルにある設定を入力してください。
                </Typography>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"Primaryキーカラム: "} tooltipText={"daoのプロパティにある「geframe.dao.PrimaryKey」を指しています。これはプライマリキーのカラム名です。"}/>
                  
                  <TextField
                    placeholder='Primaryキーカラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.primaryKey'}
                    value={setting.reservedWords.primaryKey}
                    inputRef={requiredRefs.reservedWords.primaryKey}
                    onChange={(e) => {setPrimaryKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"Exclusiveキーカラム: "} tooltipText={"daoのプロパティにある「geframe.dao.ExclusiveKey」を指しています。これはExclusiveキーのカラム名です。"}/>
                  
                  <TextField
                    placeholder='Exclusiveキーカラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.exclusiveKey'}
                    value={setting.reservedWords.exclusiveKey}
                    inputRef={requiredRefs.reservedWords.exclusiveKey}
                    onChange={(e) => {setExclusiveKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"会社コードカラム: "} tooltipText={"daoのプロパティにある「geframe.dao.CompanyCodeKey」を指しています。これは会社コードのカラム名です。"}/>
                  
                  <TextField
                    placeholder='会社コードカラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.companyCodeKey'}
                    value={setting.reservedWords.companyCodeKey}
                    inputRef={requiredRefs.reservedWords.companyCodeKey}
                    onChange={(e) => {setCompanyCodeKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"登録者IDカラム: "} tooltipText={"daoのプロパティにある「geframe.dao.InsertedUserIDKey」を指しています。これは保存者のカラム名です。"}/>
                  
                  <TextField
                    placeholder='登録者IDカラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.insertedUserIDKey'}
                    value={setting.reservedWords.insertedUserIDKey}
                    inputRef={requiredRefs.reservedWords.insertedUserIDKey}
                    onChange={(e) => {setInsertedUserIDKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"登録日付カラム: "} tooltipText={"daoのプロパティにある「geframe.dao.InsertedDateKey」を指しています。これは保存日付のカラム名です。"}/>
                  
                  <TextField
                    placeholder='登録日付カラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.insertedDateKey'}
                    value={setting.reservedWords.insertedDateKey}
                    inputRef={requiredRefs.reservedWords.insertedDateKey}
                    onChange={(e) => {setInsertedDateKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"更新者IDカラム: "} tooltipText={"daoのプロパティにある「geframe.dao.UpdatedUserIDKey」を指しています。これは更新者のカラム名です。"}/>
                  
                  <TextField
                    placeholder='更新者IDカラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.updatedUserIDKey'}
                    value={setting.reservedWords.updatedUserIDKey}
                    inputRef={requiredRefs.reservedWords.updatedUserIDKey}
                    onChange={(e) => {setUpdatedUserIDKey(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"更新日付カラム: "} tooltipText={"daoのプロパティにある「geframe.dao.UpdatedDateKey」を指しています。これは更新者のカラム名です。"}/>
                  
                  <TextField
                    placeholder='更新日付カラム'
                    fullWidth={true}
                    size="small"
                    error={errorField === 'reservedWords.updatedDateKey'}
                    value={setting.reservedWords.updatedDateKey}
                    inputRef={requiredRefs.reservedWords.updatedDateKey}
                    onChange={(e) => {setUpdatedDateKey(e.target.value)}}
                  />
                </Box>
              </TabPanel>
              <TabPanel value="4">
                <Typography textAlign={"left"} fontSize={14} sx={{color: "#999797"}}marginBottom={2}>
                  このタブは変換されたアウトプットのフォーマットに関係する項目です。
                </Typography>
                <Box marginTop="10px" textAlign="start">
                  <CustomLabel label={"ダウンロードされたファイルのエンコーディング: "} tooltipText={"ダウンロードされたファイルのエンコーディングになります。"}/>
                  
                  <TextField
                    placeholder='ダウンロードされたファイルのエンコーディング'
                    fullWidth={true}
                    data-testid="transform-encoding"
                    name="ダウンロードされたファイルのエンコーディング"
                    size="small"
                    value={setting.transform.encoding}
                    onChange={(e) => {setTransformEncoding(e.target.value)}}
                  />
                </Box>
                <Box marginTop="10px" textAlign="start">
                  
                  <div style={{display: "flex", flexDirection: "column"}}>
                    <FormControlLabel
                        label={<b>項目のエイリアスを仮想項目名に上書きする</b>}
                        control={<Checkbox />}
                        checked={setting.transform.forceAliasColumn}
                        onChange={(e) => {setForceAliasColumn((e.target as HTMLInputElement).checked)}}
                      />
                      <Typography fontSize={12} sx={{color: "#999797"}}>変換されたGEF-JDBCのAPIコードが、SELECT句の場合、項目のエイリアスを仮想表項目名に上書きします。trueの場合、エイリアスは仮想項目名になります、falseの場合はSQLクエリに指定されるエイリアスになります。</Typography>
                  </div>
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <div style={{display: "flex", flexDirection: "column"}}>
                    <FormControlLabel
                      label={<b>引数の値をバインドする際にMapを使用するかどうか</b>}
                      control={<Checkbox />}
                      checked={setting.transform.useExpMap}
                      onChange={(e) => {setUseExpMap((e.target as HTMLInputElement).checked)}}
                    />
                    <Typography fontSize={12} sx={{color: "#999797"}}>変換されたGEF-JDBCのAPIコードにある引数の値はMapにするか、それともMAPにするか？ TRUEの場合はMAPになります。FALSEの場合は配列になります。</Typography>
                  </div>
                </Box>
                <Box marginTop="10px" textAlign="start">
                  <div style={{display: "flex", flexDirection: "column"}}>
                    <FormControlLabel
                      label={<b>エンティティ定義の外部キーを使用する</b>}
                      control={<Checkbox />}
                      checked={setting.transform.useForeignKey}
                      onChange={(e) => {setUseForeignKey((e.target as HTMLInputElement).checked)}}
                    />
                    <Typography fontSize={12} sx={{color: "#999797"}}>変換されたGEF-JDBCのAPIコードにエンティティ定義にある外部キーの情報を
使用するかどうか。使用する場合、join句の条件はエンティティ定義の外部キー名を使用します。</Typography>
                  </div>
                </Box>
                
                <Box marginTop="10px" textAlign="start">
                  <div style={{display: "flex", flexDirection: "column"}}>
                    <FormControlLabel
                      label={<b>SQL処理にエンティティ定義情報を使用する</b>}
                      control={<Checkbox />}
                      disabled={true}
                      checked={setting.transform.entityQuery}
                      onChange={(e) => {setEntityQuery((e.target as HTMLInputElement).checked)}}
                    />
                    <Typography fontSize={12} sx={{color: "#999797"}}>変換されたGEF-JDBCのAPIコードはエンティティ定義情報を使用する場合、
GEF-JDBCのエンティティクエリＡＰＩを使用し、実行する時にエンティティ定義にある情報を使用することになります。使用しない場合、ダイレクトクエリを使用し、エンティティ定義にある情報を利用せず、SQLを実行します。
エンティティ定義を使用する場合、GEF-JDBCの特定な機能を得られます。</Typography>
                  </div>
                </Box>
              </TabPanel>
            </TabContext>
          </Box>
        </Box>
      </Box>
    </Box>
    
    <div id="loadingOverlay2" className="overlay">
        <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%'}}>
            <div className="spinner"></div>
            <div style={{ paddingLeft: '20px', paddingTop: '20px' }}>確認中...</div>
        </div>
    </div>
  </Dialog>
  );
}

export default SettingDialog;