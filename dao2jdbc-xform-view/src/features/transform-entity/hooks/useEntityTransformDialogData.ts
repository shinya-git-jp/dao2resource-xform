import React from "react";
import { useSettingStore } from "../../../store/setting-store";
import { convertEntity2Xml, extractEntity, fetchEntityIdByDatabaseId } from "../../../services/transform-service";
import * as yaml from 'js-yaml';
import { Buffer } from "buffer";
import JSZip from 'jszip';

var currentPage = 1;
var itemsPerPage = 10;
var containerState = [];
export const useEntityTransformDialogData = (ids: EntityCondition[]) => {
    
    const encodeBase64SettingFile = (setting) => {
        const yamlStr = yaml.dump(setting);
        const base64String = Buffer.from(yamlStr).toString('base64');
        return base64String;
    }
    const [finalArray, setFinalArray] = React.useState([]);
    const [jsonArray, setJsonArray] = React.useState([]);
    const setting = useSettingStore((state: { globalSetting: any }) => state.globalSetting);
    const [isConverted, setIsConverted] = React.useState(false);
    const [openSettingDialog, setOpenSettingDialog] = React.useState(false);
    const [isCompleted, setIsComplete] = React.useState(false)
    const [processBarLabel, setProcessBarLabel] = React.useState('処理中...');
    
    React.useEffect(()=> {
        if (!isConverted) {
            displayJSON(jsonArray);
            setIsCOnvertButtonDisabled(false);
        }else {
            processConvertedJsonArray(jsonArray);
        }
    }, [jsonArray])
    const showLoading = () => {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.style.display = 'flex';
        }
    }
    const hideLoading = () => {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.style.display = 'none';
        }
    }
    const loadDatabaseIds = async ()=> {
        showLoading();
        document.getElementById("jsonDisplay").style.display = "block";
        document.getElementById("loading").style.display = "none";
        const result = new Map();
        const base64Setting = encodeBase64SettingFile(setting);
        let count = 0;
        if (ids.length == 0) {
            try {
                const databaseChilds = await fetchEntityIdByDatabaseId(
                        "",
                        setting
                ); 
                for (const entityId of databaseChilds) {
                    result.set(entityId,{
                        id: entityId,
                        type: "entityId",
                        description: "エンティティID: "+entityId,
                        setting: setting
                    });
                    setJsonArray(Array.from(result.values()).map((value) => value));
                    count += 1;
                    setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                }
                hideLoading();
            } catch(error) {
                hideLoading();
            }
            
        } else {
            const promises = ids.map(async (id)=> {
                if (id.type == "entityId") {
                    result.set(id.description, {
                        id: id.description,
                        type: "entityId",
                        description: "エンティティID: "+id.description,
                        setting: id.setting
                    });
                    setJsonArray(Array.from(result.values()).map((value) => value));
                    count += 1;
                    setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                } else if (id.type === "databaseId") {
                    const databaseChilds = await fetchEntityIdByDatabaseId(
                        id.description,
                        setting
                    ); 
                    for (const entityId of databaseChilds) {
                        result.set(entityId,{
                            id: entityId,
                            type: "entityId",
                            description: "エンティティID: "+entityId + "(データベースID: " + id.description + ")",
                            setting: id.setting
                        });
                        setJsonArray(Array.from(result.values()).map((value) => value));
                        count += 1;
                        setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                    }
                }
            });
            await Promise.all(promises);
            hideLoading();
        }
        
    }
    const [isConvertButtonDisabled, setIsCOnvertButtonDisabled] = React.useState(true);
    const displayJSON = (jsonArray) => {
        const displayElement = document.getElementById('jsonDisplay');
        displayElement.innerHTML = ''; 

        const start = (currentPage - 1) * itemsPerPage;
        const end = start + itemsPerPage;
        const paginatedArray = jsonArray.slice(start, end);
        paginatedArray.forEach((obj, index) => {
            const container = document.createElement('div');
            container.className = 'json-container';
            let html = `
                ${obj.description}
            `;
			container.innerHTML = html;
            displayElement.appendChild(container);
        });

        displayPagination(jsonArray);
        displayPageInfo(jsonArray);
    }
    const searchJSON = () => {
        const searchTerm = (document.getElementById('searchByDescription') as HTMLInputElement).value.toLowerCase();
        const filteredArray = jsonArray.filter(obj => {
            const descriptionMatch = obj.description.toLowerCase().includes(searchTerm);
            return descriptionMatch;
        });
        if (!isConverted) {
            displayJSON(filteredArray);
        } else {
            processConvertedJsonArray(filteredArray);
        }
    }

    const onSearch = () => {
        currentPage = 1;
        searchJSON();
    }
    const displayPagination = (filteredArray) => {
        const paginationElement = document.getElementById('pagination');
        paginationElement.innerHTML = ''; 

        const totalPages = Math.ceil(filteredArray.length / itemsPerPage);

        const prevBtn = document.createElement('button');
        prevBtn.className = `pagination-btn ${currentPage === 1 ? 'disabled' : ''}`;
        prevBtn.textContent = '前へ';
        if (currentPage === 1) {
            prevBtn.disabled = true;
        }
        prevBtn.onclick = () => changePage(currentPage - 1, filteredArray);
        paginationElement.appendChild(prevBtn);

        const nextBtn = document.createElement('button');
        nextBtn.className = `pagination-btn ${currentPage >= totalPages ? 'disabled' : ''}`;
        nextBtn.textContent = '次へ';
        if (currentPage >= totalPages) {
            nextBtn.disabled = true;
        }
        nextBtn.onclick = () => changePage(currentPage + 1, filteredArray);
        paginationElement.appendChild(nextBtn);
    }
    const displayPageInfo = (filteredArray) => {
        const pageInfoElement = document.getElementById('page-info');
        const totalItems = filteredArray.length;
        const start = (currentPage - 1) * itemsPerPage + 1;
        const end = Math.min(currentPage * itemsPerPage, totalItems);
		if (start <= end) {
			pageInfoElement.textContent = `${currentPage} / ${Math.ceil(totalItems / itemsPerPage)}ページ（${start}-${end}件 / ${totalItems}件）`;
		} else {
			pageInfoElement.textContent = `0 / ${Math.ceil(totalItems / itemsPerPage)}ページ（0-${end}件 / ${totalItems}件）`;
		}
    }
    const changePage = (newPage, filteredArray) => {
        if (newPage < 1 || newPage > Math.ceil(filteredArray.length / itemsPerPage)) return;
        currentPage = newPage;
        if (!isConverted) {
            displayJSON(filteredArray);
        } else {
            containerState = []
            processConvertedJsonArray(filteredArray);
        }
    }
    const changePaginationRange = () => {
		itemsPerPage = (Number)((document.getElementById('paginationNumber') as HTMLInputElement).value);
		currentPage=1;
		searchJSON();
	}
    const [processBar, setProcessBar] = React.useState(false);
    React.useEffect(() => {
        loadDatabaseIds();
      }, []);
    const handleOpenSettingDialog = (event) => {
        setOpenSettingDialog(true);
        event.stopPropagation();
    }
    const processConvertedJsonArray = async (array)=> {
        const displayElement = document.getElementById('jsonDisplay');
        displayElement.innerHTML = ''; 
        
        const start = (currentPage - 1) * itemsPerPage;
        const end = start + itemsPerPage;
        const paginatedArray = array.slice(start, end);
        displayPagination(array);
        displayPageInfo(array);
        for (let index = 0; index < paginatedArray.length; index++) {
            const obj = paginatedArray[index];
            const container = document.createElement('div');
            container.className = 'json-container';
            container.id = `json-${index}`;
            container.onclick = () => toggleDetails(index);
            if (obj.xml) {
                const xml = atob(obj.xml).replace(/</g, "&lt;").replace(/>/g, "&gt;");
                let html = "";
                
                if (!containerState.includes(index)) {
                    html = `
                        <div class="json-header">
                            <div class="Json-header-title">
                                <span>▶</span>
                                ${obj.description}
                            </div>
                            
                        </div>
                        <div id="details-${index}" class="json-details hidden" onclick="event.stopPropagation();">
                        <div class="apiCode"> <b>Entity XML</b> <br> <span class='subtitleLabel'>エンティティXML</span></div>
                                <div class="json-item">
                                <span class="json-label">Entity XML:</span> 
                                <span class="json-value">
                                    <span id="xml-${index}"><pre>${xml}</pre></span>
                                </span>
                                <span class="json-copy-button">
                                    <button class="copy-btn" id="xml-${index}">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
                                            <path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
                                            <path d="M5.5 1.5h-1v1h1v-1z"/>
                                        </svg>
                                        コピー
                                    </button>
                                </span>
                            </div>
                        </div>
                    `;
                } else {
                    html = `
                        <div class="json-header">
                            <div class="Json-header-title">
                                <span>▼</span>
                                ${obj.description}
                            </div>
                            
                        </div>
                        <div id="details-${index}" class="json-details" onclick="event.stopPropagation();">
                        <div class="apiCode"> <b>Entity XML</b> <br> <span class='subtitleLabel'>エンティティXML</span></div>
                                <div class="json-item">
                                <span class="json-label">Entity XML:</span> 
                                <span class="json-value">
                                    <span id="xml-${index}"><pre>${xml}</pre></span>
                                </span>
                                <span class="json-copy-button">
                                    <button class="copy-btn" id="xml-${index}">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
                                            <path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
                                            <path d="M5.5 1.5h-1v1h1v-1z"/>
                                        </svg>
                                        コピー
                                    </button>
                                </span>
                            </div>
                        </div>
                    `;
                }
               
                container.innerHTML = html;
                
                // Array.from(container.getElementsByClassName("setting-btn")).forEach(element => {
                //     element.addEventListener("click", (event) => {
                //         handleOpenSettingDialog(event);
                //     });
                // });
                Array.from(container.getElementsByClassName("copy-btn")).forEach(element => {
                    if (element.getAttribute("onclick") == null) {
                        element.addEventListener("click", (event) => {
                            copyToClipboard(element.id);
                        });
                    }
                });
                displayElement.appendChild(container);
            }
            
           
        }
        
        displayPagination(jsonArray);
        displayPageInfo(jsonArray);
        setFinalArray(jsonArray);
        setIsComplete(true);
    }
    const copyToClipboard = (elementId) => {
        // const textToCopy = document.getElementById(elementId).textContent;
        // navigator.clipboard.writeText(textToCopy).then(() => {
        //     showNotification('Copied to clipboard!');
        // }).catch(err => {
        //     console.error('Could not copy text: ', err);
        // });
        const div = document.getElementById(elementId);
        if (!div) return;

        const range = document.createRange();
        range.selectNodeContents(div);

        const selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
        try {
            const success = document.execCommand('copy');
            if (success) {
                showNotification('Copied to clipboard!');
            } else {
                alert("Ctrl + Cでコピーしてください。");
            }
        } catch (err) {
            alert("Ctrl + Cでコピーしてください。");
        }
		event.stopPropagation();
    }
    const showNotification = (message) => {
        const notification = document.getElementById('notification');
        notification.textContent = message;
        notification.classList.add('show');
        
        // Hide notification after 2 seconds
        setTimeout(() => {
            notification.classList.remove('show');
        }, 2000);
    }
    const toggleDetails = (index) =>{
        const container = document.getElementById(`json-${index}`);
        const toggleBtn = container.firstElementChild.firstElementChild.firstElementChild;
        const details = document.getElementById(`details-${index}`);
        if (details.classList.contains('hidden')) {
            details.classList.remove('hidden');
            toggleBtn.innerHTML = '▼';
            containerState.push(index);
        } else {
            details.classList.add('hidden');
            toggleBtn.innerHTML = '▶';
            containerState = containerState.filter(item => item !== index);
        }
    }
    const convert = async () => {
        setIsCOnvertButtonDisabled(true)
        await convertEntityIdToXml();
    }
    const convertEntityIdToXml = async () => {
        setIsConverted(true);
        setProcessBarLabel("変換中。。。");
        setProcessBar(true);
        document.getElementById("jsonDisplay").style.display = "none";
        document.getElementById("loading").style.display = "block";
        const tempJsonArray = [...jsonArray];
        const result = []
        let count = 0;
        currentPage = 1;
        displayPagination(result);
        displayPageInfo(result);
        const promises = tempJsonArray.map(async (json) => {
            let selectedSetting = json.setting;
            if (selectedSetting == undefined || selectedSetting == "") {
                selectedSetting = setting;
            }
            try {
                const entity = await extractEntity({
                        entityId: json.id,
                        type: "entityId"
                    }, selectedSetting);
                const apiCodes = await convertEntity2Xml(entity, selectedSetting);
                result.push({
                    description: json.description,
                    name: entity["name"],
                    xml: apiCodes
                });
                count += 1;
                setProcessBarLabel("変換中(" + count + " 件の変換は完了しました)。。。");
                document.getElementById("jsonDisplay").style.display = "block";
                document.getElementById("loading").style.display = "none";
                setJsonArray(Array.from(result));
            } catch (error) {
                console.error("Error:", error);
            }
        });
        await Promise.all(promises);
        document.getElementById("loading").style.display = "none";
        setProcessBar(false);
        setJsonArray(Array.from(result));
    }
    
    const createZipFromStrings = async (files: { name: string, content: string }[]): Promise<Blob> => {
        const zip = new JSZip();
        files.forEach(file=> {
            zip.file(file.name, file.content)
        })
        return await zip.generateAsync({ type: 'blob' });
    }
    const downloadZipFile = async (blob: Blob, filename: string) => {
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      }
    const downloadFile = async () => {
        setProcessBarLabel("処理中...");
        showLoading();
       let userInput = prompt("ファイル名を入力してください(拡張子を含まない):（デフォルト：output）");

        if (userInput !== null && userInput.trim() !== "") {
            userInput = userInput.trim();
        } else {
            userInput = "output";
        }
        try {
            const xmls = [];
            finalArray.forEach((obj, index) => {
                const fileName = `${obj.description}.entity`
                const file = {
                    name: fileName,
                    content: obj.xml
                };
                xmls.push(file);
            });
            const zipBlob = await createZipFromStrings(xmls);
            await downloadZipFile(zipBlob, userInput + ".zip");
            hideLoading();
        } catch(error) {
            hideLoading();
        }
    }

    return {
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
    };
}