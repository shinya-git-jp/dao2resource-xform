import '../../../styles/report.css';
import Box from '@mui/material/Box';
import { Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, FormControlLabel, IconButton, InputLabel, MenuItem, Select, Skeleton, TextField, Tooltip } from '@mui/material';
import { Close } from '@mui/icons-material';
import InfoIcon from '@mui/icons-material/Info';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TitleTreeItem } from '../../../utils/utils';
import useTransformDialogData from '../hooks/useTransformDialogData';

const fadeInOutStyles = {
    animation: 'fadeInOut 2s infinite',
};

interface Props {
    scripts: Script[];
}

export const TransformDialog: React.FC<Props> = ({scripts}) => {
    const {
        transformEncoding,
        setTransformEncoding,
        forceAliasColumn,
        setForceAliasColumn,
        entityQuery,
        setEntityQuery,
        useForeignKey,
        setUseForeignKey,
        useExpMap,
        setUseExpMap,
        processBarLabel,
        openSettingDialog,
        setOpenSettingDialog,
        isCompleted,
        isConvertButtonDisabled,
        processBar,
        copyToClipboard,
        downloadFile,
        searchJSON,
        changePaginationRange,
        apply,
        convert,
        locale,
        setLocale,
        country1,
        setCountry1,
        country2,
        setCountry2,
        country3,
        setCountry3,
        country4,
        setCountry4,
        country5,
        setCountry5,
        primaryKey,
        setPrimaryKey,
        exclusiveKey,
        setExclusiveKey,
        companyCodeKey,
        setCompanyCodeKey,
        insertedUserIDKey,
        setInsertedUserIDKey,
        insertedDateKey,
        setInsertedDateKey,
        updatedUserIDKey,
        setUpdatedUserIDKey,
        updatedDateKey,
        setUpdatedDateKey,
        onSearch
    } = useTransformDialogData(scripts);
    
    const closeDialog = ()=> {
        (document.querySelector('.ve-overlay') as HTMLElement).style.display = 'none';
        (document.querySelector('.ve-dialog') as HTMLElement).style.display = 'none';
		document.body.style.overflow = 'auto';
    }
    return (
        <div>
            <div id="header">
                <h1>Dao2Jdbc Code XForm Report</h1>

                <div id="searchContainer">
                    <input type="text" id="searchByDescription" placeholder="仮想表IDで検索" onKeyUp={onSearch}/>

                    <select id="searchByType" onChange={onSearch}>
                        <option value="">CRUDタイプ</option>
                        <option value="SELECT">Select</option>
                        <option value="UPDATE">Update</option>
                        <option value="INSERT">Insert</option>
                        <option value="DELETE">Delete</option>
                    </select>
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
                }}  style={fadeInOutStyles}>
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
                maxWidth="xl"
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
                        <Box textAlign="left" marginTop={2} paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5}}>
                            <SimpleTreeView>
                            <TitleTreeItem itemId="setting" label="言語情報">
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel>使用する言語: </InputLabel>
                                    <Tooltip title="転換する時に使用される言語です。ツールを使う時に何の言語で出力するための設定。localeはSLocalizationにあるリソース名に依存しますので、ユニークキー名や結合キー名などはlocaleの値次第になります。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='locale'
                                    fullWidth={true}
                                    size="small"
                                    value={locale}
                                    onChange={(e) => {setLocale(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>country1の言語: </InputLabel>
                                    <Tooltip title="gef-daoプロパティにある「geframe.jk2.Locale.Language1」を指しています。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='country1の言語'
                                    fullWidth={true}
                                    size="small"
                                    value={country1}
                                    onChange={(e) => {setCountry1(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel>country2の言語: </InputLabel>
                                    <Tooltip title="gef-daoプロパティにある「geframe.jk2.Locale.Language2」を指しています。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='country2の言語'
                                    fullWidth={true}
                                    size="small"
                                    value={country2}
                                    onChange={(e) => {setCountry2(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel>country3の言語: </InputLabel>
                                    <Tooltip title="gef-daoプロパティにある「geframe.jk2.Locale.Language3」を指しています。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='country3の言語'
                                    fullWidth={true}
                                    size="small"
                                    value={country3}
                                    onChange={(e) => {setCountry3(e.target.value)}}
                                />
                                </Box>
                                
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel>country4の言語: </InputLabel>
                                    <Tooltip title="gef-daoプロパティにある「geframe.jk2.Locale.Language4」を指しています。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='country4の言語'
                                    fullWidth={true}
                                    size="small"
                                    value={country4}
                                    onChange={(e) => {setCountry4(e.target.value)}}
                                />
                                </Box>
                                
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel>country5の言語: </InputLabel>
                                    <Tooltip title="gef-daoプロパティにある「geframe.jk2.Locale.Language5」を指しています。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='country5の言語'
                                    fullWidth={true}
                                    size="small"
                                    value={country5}
                                    onChange={(e) => {setCountry5(e.target.value)}}
                                />
                                </Box>
                            </TitleTreeItem> 
                            </SimpleTreeView>
                            
                        </Box>
                        <Box textAlign="left" marginTop={2} paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5}}>
                            <SimpleTreeView>
                            <TitleTreeItem itemId="setting" label="予約言語設定">
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>Primaryキーカラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.PrimaryKey」を指しています。これはプライマリキーのカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='Primaryキーカラム'
                                    fullWidth={true}
                                    size="small"
                                    value={primaryKey}
                                    onChange={(e) => {setPrimaryKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>Exclusiveキーカラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.ExclusiveKey」を指しています。これはExclusiveキーのカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='Exclusiveキーカラム'
                                    fullWidth={true}
                                    size="small"
                                    value={exclusiveKey}
                                    onChange={(e) => {setExclusiveKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>会社コードカラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.CompanyCodeKey」を指しています。これは会社コードのカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='会社コードカラム'
                                    fullWidth={true}
                                    size="small"
                                    value={companyCodeKey}
                                    onChange={(e) => {setCompanyCodeKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>登録者IDカラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.InsertedUserIDKey」を指しています。これは保存者のカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='登録者IDカラム'
                                    fullWidth={true}
                                    size="small"
                                    value={insertedUserIDKey}
                                    onChange={(e) => {setInsertedUserIDKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>登録日付カラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.InsertedDateKey」を指しています。これは保存日付のカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='登録日付カラム'
                                    fullWidth={true}
                                    size="small"
                                    value={insertedDateKey}
                                    onChange={(e) => {setInsertedDateKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>更新者IDカラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.UpdatedUserIDKey」を指しています。これは更新者のカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='更新者IDカラム'
                                    fullWidth={true}
                                    size="small"
                                    value={updatedUserIDKey}
                                    onChange={(e) => {setUpdatedUserIDKey(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", textAlign: "center"}}>
                                    <InputLabel required>更新日付カラム: </InputLabel>
                                    <Tooltip title="daoのプロパティにある「geframe.dao.UpdatedUserIDKey」を指しています。これは更新者のカラム名。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    required
                                    placeholder='更新日付カラム'
                                    fullWidth={true}
                                    size="small"
                                    value={updatedDateKey}
                                    onChange={(e) => {setUpdatedDateKey(e.target.value)}}
                                />
                                </Box>
                            </TitleTreeItem>
                            </SimpleTreeView>
                            
                        </Box>
                        <Box textAlign="left" marginTop={2} paddingTop="50px" sx={{border: "1px solid #c9cdd1", paddingLeft: 3, paddingRight: 3, paddingTop: 2, paddingBottom: 2, borderRadius: 5}}>
                            <SimpleTreeView>
                            <TitleTreeItem itemId="setting" label="アウトプット情報">
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex"}}>
                                    <InputLabel>ダウンロードされたファイルのエンコーディング: </InputLabel>
                                    <Tooltip title="ダウンロードされたファイルのエンコーディングになります。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                <TextField
                                    placeholder='ダウンロードされたファイルのエンコーディング'
                                    fullWidth={true}
                                    size="small"
                                    value={transformEncoding}
                                    onChange={(e) => {setTransformEncoding(e.target.value)}}
                                />
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", alignItems: "center"}}>
                                    <FormControlLabel
                                    label="SQL処理にエンティティ定義情報を使用する"
                                    control={<Checkbox />}
                                    disabled={true}
                                    checked={entityQuery}
                                    onChange={(e) => {setEntityQuery((e.target as HTMLInputElement).checked)}}
                                    />
                                    <Tooltip title="転換されたGEF-JDBCのＡＰＩコードはエンティティ定義情報を使用する場合、
                GEF-JDBCのエンティティクエリＡＰＩを使用し、実行する時にエンティティ定義にある情報を使用することになります。使用しない場合、ダイレクトクエリを使用し、エンティティ定義にある情報を利用せず、SQLを実行します。
                エンティティ定義を使用する場合、GEF-JDBCの特定な機能を得られます。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                
                                <div style={{display: "flex", alignItems: "center"}}>
                                    <FormControlLabel
                                        label="項目のエイリアスを仮想項目名に上書きする"
                                        control={<Checkbox />}
                                        checked={forceAliasColumn}
                                        onChange={(e) => {setForceAliasColumn((e.target as HTMLInputElement).checked)}}
                                    />
                                    <Tooltip title="転換されたGEF-JDBCのAPIコードに、SELECT句の場合、項目のエイリアスを仮想表項目名に上書きする。trueの場合、エイリアスは仮想項目名になります、falseの場合はSQLクエリに指定されるエイリアスになります。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", alignItems: "center"}}>
                                    <FormControlLabel
                                    label="引数の値をバインドする際にMapを使用するかどうか"
                                    control={<Checkbox />}
                                    checked={useExpMap}
                                    onChange={(e) => {setUseExpMap((e.target as HTMLInputElement).checked)}}
                                    />
                                    <Tooltip title="転換されたGEF-JDBCのAPIコードにある引数の値はMapにするか、それともMAPにするか？ FALSEの場合は配列になります。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                </Box>
                                <Box marginTop="10px" textAlign="start">
                                <div style={{display: "flex", alignItems: "center"}}>
                                    <FormControlLabel
                                    label="エンティティ定義の外部キーを使用する"
                                    control={<Checkbox />}
                                    checked={useForeignKey}
                                    onChange={(e) => {setUseForeignKey((e.target as HTMLInputElement).checked)}}
                                    />
                                    <Tooltip title="転換されたGEF-JDBCのAPIコードにエンティティ定義にある外部キーの情報を
                使用するかどうか。使用する場合、join句の条件はエンティティ定義の外部キー名を使用します。" arrow>
                                    <InfoIcon fontSize='small'></InfoIcon>
                                    </Tooltip>
                                </div>
                                </Box>
                            </TitleTreeItem>
                            </SimpleTreeView>
                        </Box>
                    </Box>
                </DialogContent>
                <DialogActions>
                <Button onClick={()=>{apply(); setOpenSettingDialog(false)}} autoFocus>
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
            <div className="ve-overlay" id="ve-overlay" onClick={closeDialog}>
                <div className="ve-dialog" onClick={(event)=>{event.stopPropagation()}}>
                    <h1>仮想表定義情報</h1>
                    
                    <div className="tab-header">
                        <div className="tab" data-target="tab1">全般</div>
                        <div className="tab" data-target="tab2">メインエンティティ</div>
                    </div>
                    <div className="tab-content" id="tab1">
                        <div className="ve-header">
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    仮想表名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-displayname">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    カテゴリ名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-categoryName">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    仮想表ID：
                                </div>
                                <div className="ve-detail-value" id="ve-info-veid">
                                </div>
                            </div>
                            
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    カテゴリID：
                                </div>
                                <div className="ve-detail-value" id="ve-info-categoryid">
                                </div>
                            </div>
                            
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    仮想表タイプ：
                                </div>
                                <div className="ve-detail-value" id="ve-info-vetype">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                </div>
                                <div className="ve-detail-value">
                                </div>
                            </div>
                            <span style={{display:"none"}} id="ve-info-absolutevecode"> </span>
                        </div>
                        
                        <div className="table-container" id="ve-info-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> 仮想項目 </span>
                            </div>
                            <div id="ve-info-columns">
                            </div>
                        </div>
                        <div>
                            <button className="copy-btn" id="copy-absolute-ve-btn" style={{marginTop: "30px", marginLeft:"-2px", fontWeight:"bold"}} onClick={()=>{copyToClipboard('ve-info-absolutevecode')}}>完全仮想項目をコピー</button>
                        </div>
                        
                        <div className="ve-header">
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    外部キー名：
                                </div>
                                <div className="ve-detail-value">
                                    <div  className="custom-combobox"> 
                                        <select id="ve-foreign-keys">
                                            <option> - </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    参照エンティティ名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-foreign-keys-ref">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    外部キータイプ：
                                </div>
                                <div className="ve-detail-value" id="ve-info-foreign-keys-join-type">
                                </div>
                            </div>
                            
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    
                                </div>
                                <div className="ve-detail-value">
                                </div>
                            </div>
                        </div>
                        
                        <div className="table-container" id="ve-info-foreign-keys-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> 外部キー </span>
                            </div>
                            <div id="ve-info-foreign-keys-columns">
                            </div>
                        </div>
                        
                        <div className="table-container" style={{marginTop: "50px"}} id="ve-info-filter-conditions-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> フィルター条件 </span>
                            </div>
                            <div id="ve-info-filter-conditions">
                            </div>
                        </div>
                        
                        <div className="table-container" style={{marginTop: "50px"}} id="ve-info-search-conditions-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> 検索条件 </span>
                            </div>
                            <div id="ve-info-search-conditions">
                            </div>
                        </div>
                        
                        
                        <div className="table-container" style={{marginTop: "50px"}} id="ve-info-sort-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> ソート項目 </span>
                            </div>
                            <div id="ve-info-sort-columns">
                            </div>
                        </div>
                        
                        
                        <div className="table-container" style={{marginTop: "50px"}} id="ve-info-group-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> グループ項目 </span>
                            </div>
                            <div id="ve-info-group-columns">
                            </div>
                        </div>
                    </div>
                    <div className="tab-content" id="tab2"> 
                        <div className="ve-header">
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    メインエンティティ名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-name">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    データベース名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-database">
                                </div>
                            </div>
                        </div>
                        <div className="table-container" id="ve-info-entity-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> メインエンティティ項目 </span>
                            </div>
                            <div id="ve-info-entity-columns">
                            </div>
                        </div>
                        <div className="ve-header">
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    ユニークキー名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-name">
                                    <div  className="custom-combobox"> 
                                        <select id="ve-entity-unique-keys">
                                            <option> - </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                </div>
                                <div className="ve-detail-value">
                                </div>
                            </div>
                        </div>
                        
                        <div className="table-container" id="ve-info-entity-unique-keys-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> ユニークキー </span>
                            </div>
                            <div id="ve-info-entity-unique-keys-columns">
                            </div>
                        </div>
                        <div className="ve-header">
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    外部キー名：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-name">
                                    <div  className="custom-combobox"> 
                                        <select id="ve-entity-foreign-keys">
                                            <option> - </option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    参照エンティティ：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-foreign-keys-ref">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    外部キータイプ：
                                </div>
                                <div className="ve-detail-value" id="ve-info-entity-foreign-keys-join-type">
                                </div>
                            </div>
                            <div className="ve-detail-header">
                                <div className="ve-detail-title">
                                    
                                </div>
                                <div className="ve-detail-value">
                                </div>
                            </div>	
                        </div>
                        
                        <div className="table-container" id="ve-info-entity-foreign-keys-columns-container">
                            <div className="table-container-title">
                                <span className="table-container-title-chip"> 外部キー </span>
                            </div>
                            <div id="ve-info-entity-foreign-keys-columns">
                            </div>
                        </div>
                    </div>
                    <div>
                        <button className="close-btn" style={{fontSize: "15pt"}} onClick={closeDialog}>X</button>
                    </div>
                </div>
            </div>

            
        </div>
    );
}

export default TransformDialog;