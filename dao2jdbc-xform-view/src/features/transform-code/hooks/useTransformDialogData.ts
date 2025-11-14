import React, { useEffect } from "react";
import { http } from "../../../config/http";
import { useSettingStore } from "../../../store/setting-store";
import { useInputStore } from "../../../store/input-store";
import { convertSql2Code, extractSql, fetchVeIdByCategoryId } from "../../../services/transform-service";
import { ScriptType } from "../../../types/enum";

var currentPage = 1;
var itemsPerPage = 10;
var containerState = [];
var wehereContainerState = [];
const useTransformDialogData = (ids: Script[]) => {
    const setting = useSettingStore((state: { globalSetting: Setting }) => state.globalSetting);
    const [primaryKey, setPrimaryKey] = React.useState(setting.reservedWords.primaryKey);
    const [exclusiveKey, setExclusiveKey] = React.useState(setting.reservedWords.exclusiveKey);
    const [companyCodeKey, setCompanyCodeKey] = React.useState(setting.reservedWords.companyCodeKey);
    const [insertedUserIDKey, setInsertedUserIDKey] = React.useState(setting.reservedWords.insertedUserIDKey);
    const [insertedDateKey, setInsertedDateKey] = React.useState(setting.reservedWords.insertedDateKey);
    const [updatedUserIDKey, setUpdatedUserIDKey] = React.useState(setting.reservedWords.updatedUserIDKey);
    const [updatedDateKey, setUpdatedDateKey] = React.useState(setting.reservedWords.updatedDateKey);
    const [locale, setLocale] = React.useState(setting.locale.current);
    const [country1, setCountry1] = React.useState(setting.locale.mapping.country1);
    const [country2, setCountry2] = React.useState(setting.locale.mapping.country2);
    const [country3, setCountry3] = React.useState(setting.locale.mapping.country3);
    const [country4, setCountry4] = React.useState(setting.locale.mapping.country4);
    const [country5, setCountry5] = React.useState(setting.locale.mapping.country5);
    const [transformEncoding, setTransformEncoding] = React.useState(setting.transform.encoding);
    const [forceAliasColumn, setForceAliasColumn] = React.useState(setting.transform.forceAliasColumn);
    const [entityQuery, setEntityQuery] = React.useState(setting.transform.entityQuery);
    const [useForeignKey, setUseForeignKey] = React.useState(setting.transform.useForeignKey);
    const [useExpMap, setUseExpMap] = React.useState(setting.transform.useExpMap);
    const setScripts = useInputStore((state)  => state.setScripts);
    useEffect(()=> {
        setScripts(ids);
    }, [ids]);
	let tabs = document.querySelectorAll('.tab');
	let contents = document.querySelectorAll('.tab-content');
    const [finalArray, setFinalArray] = React.useState([]);
    const [jsonArray, setJsonArray] = React.useState([]);
    // const ids = useInputStore((state: {scripts: any})  => {return state.scripts});
    const [isConverted, setIsConverted] = React.useState(false);
    const [processBarLabel, setProcessBarLabel] = React.useState('処理中...');
    const loadCategoryIds = async ()=> {
        showLoading();
        document.getElementById("jsonDisplay").style.display = "block";
        document.getElementById("loading").style.display = "none";
        const result = new Map();
        let count = 0;  
        let promises = [];
        if (ids.length == 0) {
            try {
                const categoryChilds = await fetchVeIdByCategoryId("", setting);
                for (const categoryVeId of categoryChilds) {
                    result.set(categoryVeId,{
                        id: categoryVeId,
                        type: ScriptType.VEID,
                        description: "仮想表ID: "+categoryVeId,
                        statementTypes: ["SELECT", "UPDATE", "DELETE", "INSERT"],
                        setting: setting
                    });
                    setJsonArray(Array.from(result.values()).map((value) => value));
                    count += 1;
                    setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                }
                hideLoading();
            } catch (error) {  
                console.log(error);
                hideLoading();
            }
        } else {
            promises = ids.map(async (id) => {
                const crudType = [];
                if (id.crudTypeSelect) {
                    crudType.push("SELECT");
                } 
                if (id.crudTypeDelete) {
                    crudType.push("DELETE");
                }
                if (id.crudTypeInsert) {
                    crudType.push("INSERT");
                }
                if (id.crudTypeUpdate) {
                    crudType.push("UPDATE");
                }
                if (id.type == ScriptType.VEID) {
                    result.set(id.description, {
                        id: id.description,
                        type: ScriptType.VEID,
                        description: "仮想表ID: "+id.description + " / (" + crudType.join(", ") + ")",
                        statementTypes: crudType,
                        setting: id.setting
                    });
                    setJsonArray(Array.from(result.values()).map((value) => value));
                    count += 1;
                    setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                } else if (id.type == ScriptType.CATEGORYID) {
                    try {
                        const categoryChilds = await fetchVeIdByCategoryId(id.description, setting);
                        for (const categoryVeId of categoryChilds) {
                            result.set(categoryVeId,{
                                id: categoryVeId,
                                type: ScriptType.VEID,
                                description: "仮想表ID: "+categoryVeId + "(カテゴリID: " + id.description + ")",
                                statementTypes: crudType,
                                setting: id.setting
                            });
                            setJsonArray(Array.from(result.values()).map((value) => value));
                            count += 1;
                            setProcessBarLabel(count + '件の仮想表の取得が完了しました');
                        }
                    } catch (error) {  
                        hideLoading();
                    }
                }
            });
            await Promise.all(promises);
            hideLoading();
        }
        
    }
    const convertVeIdToApiCode = async () => {
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
        const promisesArray = [];
        const promises = tempJsonArray.map(async (json) => {
            let selectedSetting = json.setting;
            if (selectedSetting == undefined || selectedSetting == null) {
                selectedSetting = setting;
            }
            const secondPromises = json.statementTypes.map(async (statementType) => {
                try {
                    const sqls: SqlExtractionOutput = await extractSql(
                        {
                            veId: json.id,
                            crudTypes: [statementType]
                        },
                        selectedSetting
                    ); 
                    if (sqls != null) {
                        const apiCodes = await convertSql2Code(sqls, selectedSetting);
                        result.push({
                            ...apiCodes,
                            id: json.id,
                        });
                        count += 1;
                        document.getElementById("jsonDisplay").style.display = "block";
                        document.getElementById("loading").style.display = "none";
                        setProcessBarLabel("変換中(" + count + " 件の変換は完了しました)。。。");
                        setJsonArray(Array.from(result.values()).map((value) => value));
                    }
                } catch (error) {
                    console.log(error)
                }
            });
            promisesArray.push(...secondPromises);
        });
        await Promise.all(promises);
        await Promise.all(promisesArray);
        document.getElementById("loading").style.display = "none";
        setProcessBar(false);
        setJsonArray(Array.from(result.values()).map((value) => value));
    }
    React.useEffect(()=> {
        if (!isConverted) {
            displayJSON(jsonArray);
            setIsCOnvertButtonDisabled(false);
        } else {
            searchJSON();
        }
    }, [jsonArray])
	const [openSettingDialog, setOpenSettingDialog] = React.useState(false);
    const [isCompleted, setIsComplete] = React.useState(false)
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
    const replaceApiCode = (html, objIndex, itemIndex, apiType, paginatedArray, crudType) => {
		let codeWhere = paginatedArray[objIndex].apiCodes[apiType+"-WHERE"];
		let codeWherePK = paginatedArray[objIndex].apiCodes[apiType+"-WHERE_PK"];
		let codeWhereUK = paginatedArray[objIndex].apiCodes[apiType+"-WHERE_UK"];
		let code = paginatedArray[objIndex].apiCodes[apiType];
		
		if (code != undefined) {
			return html.replace('{item'+itemIndex+'}', `
				<div class="json-item">
					<span class="json-label">${replaceToCamelCase(apiType)}:</span> 
					<span class="json-value">
						<pre><span id="apiCode-${objIndex}-${itemIndex}">${code}</span></pre>
					</span>
					<span class="json-copy-button">
						<button class="copy-btn" id="apiCode-${objIndex}-${itemIndex}">
							<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
								<path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
								<path d="M5.5 1.5h-1v1h1v-1z"/>
							</svg>
								コピー
						</button>
					</span>
				</div>
			`);
		} else if (codeWhere != undefined || codeWherePK != undefined || codeWhereUK != undefined) {
			let apiCodeHtml = `<div class="json-item" style="display: flex; flex-direction: row;">
					<span class="json-label" >${replaceToCamelCase(apiType)}:  `;
			if (codeWhere != undefined) {
                if (wehereContainerState.includes(`where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    apiCodeHtml += `<div class="checkbox-container">
							<div class="checkbox-wrapper-4">
							  <input class="inp-cbx" id="where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox"/>
							  <label class="cbx" for="where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
							  <svg width="12px" height="10px">
								<use xlink:href="#check-4"></use>
							  </svg></span><span>where</span></label>
							  <svg class="inline-svg">
								<symbol id="check-4" viewbox="0 0 12 10">
								  <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
								</symbol>
							  </svg>
							</div>
						  </div>`;
                } else {
                    apiCodeHtml += `<div class="checkbox-container">
                    <div class="checkbox-wrapper-4">
                      <input class="inp-cbx" id="where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox" checked/>
                      <label class="cbx" for="where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
                      <svg width="12px" height="10px">
                        <use xlink:href="#check-4"></use>
                      </svg></span><span>where</span></label>
                      <svg class="inline-svg">
                        <symbol id="check-4" viewbox="0 0 12 10">
                          <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
                        </symbol>
                      </svg>
                    </div>
                  </div>`;
                }
				
		   }
			if (codeWherePK != undefined) {
                if (wehereContainerState.includes(`wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    apiCodeHtml += `<div class="checkbox-container">
							<div class="checkbox-wrapper-4">
							  <input class="inp-cbx" id="wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox"/>
							  <label class="cbx" for="wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
							  <svg width="12px" height="10px">
								<use xlink:href="#check-5"></use>
							  </svg></span><span>wherePK</span></label>
							  <svg class="inline-svg">
								<symbol id="check-5" viewbox="0 0 12 10">
								  <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
								</symbol>
							  </svg>
							</div>
						  </div>`;
                } else {
                    apiCodeHtml += `<div class="checkbox-container">
                    <div class="checkbox-wrapper-4">
                      <input class="inp-cbx" id="wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox" checked/>
                      <label class="cbx" for="wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
                      <svg width="12px" height="10px">
                        <use xlink:href="#check-5"></use>
                      </svg></span><span>wherePK</span></label>
                      <svg class="inline-svg">
                        <symbol id="check-5" viewbox="0 0 12 10">
                          <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
                        </symbol>
                      </svg>
                    </div>
                  </div>`;
                }
				
			}
			if (codeWhereUK != undefined) {	
                if (wehereContainerState.includes(`whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    apiCodeHtml += `<div class="checkbox-container">
                                <div class="checkbox-wrapper-4">
                                <input class="inp-cbx" id="whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox"/>
                                <label class="cbx" for="whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
                                <svg width="12px" height="10px">
                                    <use xlink:href="#check-6"></use>
                                </svg></span><span>whereUK</span></label>
                                <svg class="inline-svg">
                                    <symbol id="check-6" viewbox="0 0 12 10">
                                    <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
                                    </symbol>
                                </svg>
                                </div>
                            </div>`;
                } else {
                    apiCodeHtml += `<div class="checkbox-container">
                        <div class="checkbox-wrapper-4">
                        <input class="inp-cbx" id="whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox" type="checkbox" checked/>
                        <label class="cbx" for="whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox"><span>
                        <svg width="12px" height="10px">
                            <use xlink:href="#check-6"></use>
                        </svg></span><span>whereUK</span></label>
                        <svg class="inline-svg">
                            <symbol id="check-6" viewbox="0 0 12 10">
                            <polyline points="1.5 6 4.5 9 10.5 1"></polyline>
                            </symbol>
                        </svg>
                        </div>
                    </div>`;
                }
			}
			apiCodeHtml += `</span>
					<div class="api-code-container" >`;
			if (codeWhere != undefined) {
                let whereDisplayStatus = "flex";
                if (wehereContainerState.includes(`where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    whereDisplayStatus = "none";
                }
				apiCodeHtml += `<div style="display: `+whereDisplayStatus+`; flex-direction: row; padding: 10px 0px;" id="where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}">
							<div class="json-label" >where: </div>
							<span class="json-value" >
								<pre><span id="apiCode-${objIndex}-${itemIndex}-where">${codeWhere}</span></pre>
							</span>
							<div class="json-copy-button">
								<button class="copy-btn" id="apiCode-${objIndex}-${itemIndex}-where">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
										<path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
										<path d="M5.5 1.5h-1v1h1v-1z"/>
									</svg>
									コピー
								</button>
							</div>
						</div>`;
			}
			if (codeWherePK != undefined) {
                let whereDisplayStatus = "flex";
                if (wehereContainerState.includes(`wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    whereDisplayStatus = "none";
                }
				apiCodeHtml += `<div style="display: `+whereDisplayStatus+`; flex-direction: row; padding: 10px 0px;" id="wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}">
							<div class="json-label" >wherePK: </div>
							<span class="json-value">
								<pre><span id="apiCode-${objIndex}-${itemIndex}-wherePK">${codeWherePK}</span></pre>
							</span>
							<div class="json-copy-button">
								<button class="copy-btn" id="apiCode-${objIndex}-${itemIndex}-wherePK">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
										<path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
										<path d="M5.5 1.5h-1v1h1v-1z"/>
									</svg>
									コピー
								</button>
							</div>
						</div>`;
			}
			if (codeWhereUK != undefined) {
                let whereDisplayStatus = "flex";
                if (wehereContainerState.includes(`whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}`)) {
                    whereDisplayStatus = "none";
                }
				apiCodeHtml += `<div style="display: `+whereDisplayStatus+`; flex-direction: row; padding: 10px 0px;" id="whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}">
							<div class="json-label" >whereUK: </div>
							<span class="json-value">
								<pre><span id="apiCode-${objIndex}-${itemIndex}-whereUK">${codeWhereUK}</span></pre>
							</span>
							<div class="json-copy-button"	>
								<button class="copy-btn" id="apiCode-${objIndex}-${itemIndex}-whereUK">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
										<path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
										<path d="M5.5 1.5h-1v1h1v-1z"/>
									</svg>
									コピー
								</button>
							</div>
						</div>`;
			}
			apiCodeHtml += `</div>
			</div>`;
			return html.replace('{item'+itemIndex+'}', apiCodeHtml);
		}
		return html.replace('{item'+itemIndex+'}', "");
	}
    const replaceToCamelCase = (str) => {
		return str.toLowerCase().replace(/_./g, match => match.charAt(1).toUpperCase());;
	}
    const toggleDetails = (index) =>{
        const container = document.getElementById(`json-${index}`);
        const toggleBtn = container.firstElementChild.firstElementChild.firstElementChild;
        const details = document.getElementById(`details-${index}`);
        if (details.classList.contains('hidden')) {
            containerState.push(index);
            details.classList.remove('hidden');
            toggleBtn.innerHTML = '▼';
        } else {
            details.classList.add('hidden');
            containerState = containerState.filter(item => item !== index);
            toggleBtn.innerHTML = '▶';
        }
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
        
        setTimeout(() => {
            notification.classList.remove('show');
        }, 2000);
    }
    function hideWhere(id) {
		const display = document.getElementById(id).style.display;
		if (display == "none") {
            wehereContainerState = wehereContainerState.filter(item => item !== id);
			document.getElementById(id).style.display="flex";
		} else {
            wehereContainerState.push(id);
			document.getElementById(id).style.display="none";
		}
	}
    const onSearch = () => {
        currentPage = 1;
        searchJSON();
    }
    const searchJSON = () => {
        const searchTerm = (document.getElementById('searchByDescription') as HTMLInputElement).value.toLowerCase();
        const typeFilter = (document.getElementById('searchByType') as HTMLInputElement).value;
        const filteredArray = jsonArray.filter(obj => {
            if (obj.description == undefined) {
                return false;
            }
            const descriptionMatch = obj.description.toLowerCase().includes(searchTerm);
            let typeMatch = true;
            if (!isConverted) {
                let tempTypeMatch = false;
                for (const statementType of obj.statementTypes) {
                    if (statementType == typeFilter) {
                        tempTypeMatch = true;
                        break;
                    }
                }
                typeMatch = typeFilter === "" || tempTypeMatch;
            } else {
                typeMatch = typeFilter === "" || obj.type === typeFilter;
            }
            return descriptionMatch && typeMatch;
        });
        if (!isConverted) {
            displayJSON(filteredArray);
        } else {
            processConvertedJsonArray(filteredArray)
        }
    }
    const displayPagination = (filteredArray) => {
        const paginationElement = document.getElementById('pagination');
        paginationElement.innerHTML = ''; 

        const totalPages = Math.ceil(filteredArray.length / itemsPerPage);

        const prevBtn = document.createElement('button');
        prevBtn.className = `pagination-btn ${currentPage === 1 ? 'disabled' : ''}`;
        if (currentPage === 1) {
            prevBtn.disabled = true;
        }
        prevBtn.textContent = '前へ';
        prevBtn.onclick = () => changePage(currentPage - 1, filteredArray);
        paginationElement.appendChild(prevBtn);


        const nextBtn = document.createElement('button');
        nextBtn.className = `pagination-btn ${currentPage >= totalPages  ? 'disabled' : ''}`;
        if (currentPage >= totalPages) {
            nextBtn.disabled = true;
        }
        // nextBtn.disabled = true;
        nextBtn.textContent = '次へ';
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
            containerState = [];
            wehereContainerState = [];
            processConvertedJsonArray(filteredArray)
        }
    }
    const changePaginationRange = () => {
		itemsPerPage = (Number)((document.getElementById('paginationNumber') as HTMLInputElement).value);
		currentPage = 1;
		searchJSON();
	}
    const [processBar, setProcessBar] = React.useState(false);
    const tempSetting = {...setting, db: {...setting.db}, };
	const switchTab = (event) => {
		const targetId = event.target.getAttribute('data-target');
		tabs.forEach(tab => tab.classList.remove('active'));
		contents.forEach(content => content.classList.remove('active'));

		event.target.classList.add('active');
		document.getElementById(targetId).classList.add('active');
		checkAllTableContainerOverflow();
	}

	const checkOverflow = (resizableDiv) => {
        const isOverflowing = resizableDiv.scrollHeight > resizableDiv.clientHeight;
        if (isOverflowing) {
          resizableDiv.style.resize = 'vertical';
        } else {
          resizableDiv.style.resize = 'none';
        }
      }
	const checkAllTableContainerOverflow = () => {
		checkOverflow(document.getElementById("ve-info-entity-foreign-keys-columns-container"));
		checkOverflow(document.getElementById("ve-info-entity-unique-keys-columns-container"));
		checkOverflow(document.getElementById("ve-info-entity-columns-container"));
		checkOverflow(document.getElementById("ve-info-group-columns-container"));
		checkOverflow(document.getElementById("ve-info-sort-columns-container"));
		checkOverflow(document.getElementById("ve-info-search-conditions-container"));
		checkOverflow(document.getElementById("ve-info-filter-conditions-container"));
		checkOverflow(document.getElementById("ve-info-foreign-keys-columns-container"));
		checkOverflow(document.getElementById("ve-info-columns-container"));
	}
    React.useEffect(() => {
        loadCategoryIds();
      }, []);
    const handleOpenSettingDialog = (event) => {
        setOpenSettingDialog(true);
        event.stopPropagation();
    }
    const processConvertedJsonArray = async (array) => {
        const displayElement = document.getElementById('jsonDisplay');
        displayElement.innerHTML = ''; 

        const start = (currentPage - 1) * itemsPerPage;
        const end = start + itemsPerPage;
        const paginatedArray = array.slice(start, end);
        displayPagination([]);
        displayPageInfo([]);
        for (let index = 0; index < paginatedArray.length; index++) {
            const obj = paginatedArray[index];
            const container = document.createElement('div');
            container.className = 'json-container';
           
            container.id = `json-${index}`;
			container.onclick = () => toggleDetails(index);
            let html = '';
            if (!containerState.includes(index)) {
                html = `
                <div class="json-header">
                    <div class="Json-header-title">
                        <span>▶</span>
                        ${obj.description} / ${obj.type}
                    </div>
                    <span style="display: flex; flex-direction: row;">
                        <span>
                            <button class="setting-btn" style="margin-right:10px;">
                                <svg width="16" height="16" fill="white" viewBox="0 0 24 24">
                                    <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6"></path>
                                </svg>
                                設定
                            </button>
                        </span>
                        <span>
                            {detail_btn}
                        </span>
                    <span>
                </div>
                <div id="details-${index}" class="json-details hidden" onclick="event.stopPropagation();">
                <div class="apiCode"> <b>SQLスクリプト</b> <br> <span class='subtitleLabel'>変換前のSQLスクリプト</span></div>
                    <div class="json-item">
                        <span class="json-label">SQLスクリプト:</span> 
                        <span class="json-value">
                            <span id="sqlScript-${index}">${obj.sqlScript}</span>
                        </span>
						<span class="json-copy-button">
							<button class="copy-btn" id="sqlScript-${index}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
                                    <path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
                                    <path d="M5.5 1.5h-1v1h1v-1z"/>
                                </svg>
                                コピー
                            </button>
						</span>
                    </div>
					<div class="apiCode"> <b>APIコード</b> <br> <span class='subtitleLabel'>変換後のgef-jdbc apiコード</span></div>
					{item1}
					{item2}
					{item3}
                </div>
            `;
            } else {
                html = `
                <div class="json-header">
                    <div class="Json-header-title">
                        <span>▼</span>
                        ${obj.description} / ${obj.type}
                    </div>
                    <span style="display: flex; flex-direction: row;">
                        <span>
                            <button class="setting-btn" style="margin-right:10px;">
                                <svg width="16" height="16" fill="white" viewBox="0 0 24 24">
                                    <path d="M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6"></path>
                                </svg>
                                設定
                            </button>
                        </span>
                        <span>
                            {detail_btn}
                        </span>
                    <span>
                </div>
                <div id="details-${index}" class="json-details" onclick="event.stopPropagation();">
                <div class="apiCode"> <b>SQLスクリプト</b> <br> <span class='subtitleLabel'>変換前のSQLスクリプト</span></div>
                    <div class="json-item">
                        <span class="json-label">SQLスクリプト:</span> 
                        <span class="json-value">
                            <span id="sqlScript-${index}">${obj.sqlScript}</span>
                        </span>
						<span class="json-copy-button">
							<button class="copy-btn" id="sqlScript-${index}">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
                                    <path d="M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z"/>
                                    <path d="M5.5 1.5h-1v1h1v-1z"/>
                                </svg>
                                コピー
                            </button>
						</span>
                    </div>
					<div class="apiCode"> <b>API Code</b> <br> <span class='subtitleLabel'>変換後のgef-jdbc apiコード</span></div>
					{item1}
					{item2}
					{item3}
                </div>
            `;   
            }
           
			html = changeDetailButtonIfAbsoluteColumnExist(html, index, paginatedArray);
			container.innerHTML = html;
			if (obj.type == "SELECT") {
				html = replaceApiCode(html, index, 1, 'FIND_LIST', paginatedArray, obj.type);
				html = replaceApiCode(html, index, 2, 'FIND_RECORD', paginatedArray, obj.type);
				html = replaceApiCode(html, index, 3, 'FIND_RECORD_SET', paginatedArray, obj.type);
			} else {
				html = replaceApiCode(html, index, 1, 'EXECUTE', paginatedArray, obj.type);
				html = replaceApiCode(html, index, 2, 'EXECUTE_LIST', paginatedArray, obj.type);
				html = replaceApiCode(html, index, 3, 'EXECUTE_BATCH', paginatedArray, obj.type);
			}
            
			container.innerHTML = html;
            Array.from(container.getElementsByClassName("setting-btn")).forEach(element => {
                element.addEventListener("click", (event) => {
                    setApplyTarget(obj);

                    handleOpenSettingDialog(event);
                });
            });
            Array.from(container.getElementsByClassName("copy-btn")).forEach(element => {
                if (element.getAttribute("onclick") == null) {
                    element.addEventListener("click", (event) => {
                        copyToClipboard(element.id);
                    });
                }
            });
            
            Array.from(container.getElementsByClassName("inp-cbx")).forEach(element => {
                element.addEventListener("change", (event) => {
                    hideWhere(element.id.replace("-checkbox", ""));
                });
            });

            displayElement.appendChild(container);
           
			document.getElementById("btn-details-"+index).addEventListener('click', function(event) {
				openDialog(event, index, paginatedArray);
			});
        }
        displayPagination(array);
        displayPageInfo(array);
        setFinalArray(array);
        setIsComplete(true);
    }
    const convert = async () => {
        setIsCOnvertButtonDisabled(true)
        await convertVeIdToApiCode();
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
            let jsonString = JSON.stringify(finalArray);
            jsonString = jsonString.replace("<?", "< ?");
            jsonString = jsonString.replace(">?", "> ?");
            jsonString = jsonString.replace("<GRecord>", "&lt;GRecord&gt;");
            jsonString = jsonString.replace("Map<String, Object>", "Map&lt;String, Object&gt;");
            const response = await http.get('/template.html');
            let htmlString = await response.data;
            htmlString = htmlString.replace('${json_value}', jsonString);
            const blob = new Blob([htmlString], { type: 'text/html' });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = userInput + ".html";
            link.click();
            hideLoading();
        } catch(error) {
            hideLoading();
        }
       
        
    }
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
    const openDialog = (event, index, paginatedArray) => {
        (document.querySelector('.ve-overlay') as HTMLElement).style.display = 'flex';
        (document.querySelector('.ve-dialog') as HTMLElement).style.display = 'block';
        (document.querySelector('.ve-dialog') as HTMLElement).style.transform = 'scale(1)';
        tabs = document.querySelectorAll('.tab');
        contents = document.querySelectorAll('.tab-content');
        tabs.forEach(tab => {
            tab.addEventListener('click', switchTab);
        });
        tabs[0].classList.add('active');
        contents[0].classList.add('active');
		replaceVeInfo(index, paginatedArray);
		resetToTheFirstTab();
		document.body.style.overflow = 'hidden';
		event.stopPropagation();
    }

	const resetToTheFirstTab　= () => {
		tabs.forEach(tab => tab.classList.remove('active'));
		contents.forEach(content => content.classList.remove('active'));

		tabs[0].classList.add('active');
		contents[0].classList.add('active');
	}
	const replaceVeInfo = (objIndex, paginatedArray) => {
		const ve = paginatedArray[objIndex].virtualEntity;
		document.getElementById("ve-info-veid").innerHTML = ve.veId;
		document.getElementById("ve-info-categoryid").innerHTML = ve.categoryId;
		document.getElementById("ve-info-vetype").innerHTML = ve.veType;
		document.getElementById("ve-info-displayname").innerHTML = ve.displayName;
		document.getElementById("ve-info-categoryName").innerHTML = ve.categoryName;
		document.getElementById("ve-info-absolutevecode").innerHTML = ve.absoluteVirtualColumnCode;
		document.getElementById("ve-info-entity-name").innerHTML = ve.refEntity.name;
		document.getElementById("ve-info-entity-database").innerHTML = ve.refEntity.databaseName;
		
		let html = `
		<table>
			<thead>
				<tr>
					<th>項目名</th>
					<th>表示名</th>
					<th>集約関数</th>
					<th>初期値</th>
					<th>フォーマット</th>
					<th>エンティティ名.項目名</th>
				</tr>
			</thead>
			<tbody>`;
		ve.columns.forEach((obj, index) => {
			const refColumn = obj.refColumn != null ? obj.refColumn : '<span className="absolute-ve">完全仮想項目</span>';
			html += `<tr>
					<td>${obj.name}</td>
					<td>${obj.displayName}</td>
					<td>${obj.aggregateFunction}</td>
					<td>${obj.fixedValue}</td>
					<td>${obj.format}</td>
					<td>${refColumn}</td>
				</tr>`;
		});
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-columns").innerHTML = html;
		html = `
			<table>
			<thead>
				<tr>
					<th>項目名</th>
					<th>サイズ</th>
					<th>データタイプ</th>
					<th>少数サイズ</th>
					<th>Null可</th>
					<th>プライマリキー</th>
				</tr>
			</thead>
			<tbody>`;
		ve.refEntity.columns.forEach((obj, index) => {
			const isPrimaryKey = obj.primaryKey == true ? '<span className="absolute-ve">PK</span>' : '-';
			html += `<tr>
					<td>${obj.name}</td>
					<td>${obj.size}</td>
					<td>${obj.dataType}</td>
					<td>${obj.decimalSize}</td>
					<td>${obj.notNull}</td>
					<td>${isPrimaryKey}</td>
				</tr>`;
		});
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-entity-columns").innerHTML = html;
		html = ``;
		if (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {
			ve.refEntity.uniqueKeys.forEach((obj, index) => {
				html += `<option>${obj.name}</option>`;
			});
		} else {
			html = `<option>-</option>`;
		}

		document.getElementById("ve-entity-unique-keys").innerHTML = html;
		document.getElementById("ve-entity-unique-keys").onchange = function() {
            switchRefEntityUniqueKey(objIndex, paginatedArray, (document.getElementById("ve-entity-unique-keys") as HTMLSelectElement).value)
		}
		html = `
			<table>
			<thead>
				<tr>
					<th>項目名</th>
					<th>サイズ</th>
					<th>データタイプ</th>
					<th>少数サイズ</th>
					<th>Null可</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {
			ve.refEntity.uniqueKeys[0].uniqueKeyColumns.forEach((obj, index) => {
				const isPrimaryKey = obj.primaryKey == true ? '<span className="absolute-ve">PK</span>' : '-';
				html += `<tr>
						<td>${obj.name}</td>
						<td>${obj.size}</td>
						<td>${obj.dataType}</td>
						<td>${obj.decimalSize}</td>
						<td>${obj.notNull}</td>
					</tr>`;
			});
		}
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-entity-unique-keys-columns").innerHTML = html;
		html = ``;
		if (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {
			ve.refEntity.foreignKeys.forEach((obj, index) => {
				html += `<option>${obj.name}</option>`;
			});
		} else {
			html = `<option>-</option>`;
		}
		document.getElementById("ve-entity-foreign-keys").innerHTML = html;
		
		document.getElementById("ve-entity-foreign-keys").onchange = function() {
            switchRefEntityForeignKey(objIndex, paginatedArray, (document.getElementById("ve-entity-foreign-keys") as HTMLSelectElement).value)
		}
		if (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {
			document.getElementById("ve-info-entity-foreign-keys-ref").innerHTML = ve.refEntity.foreignKeys[0].referenceEntity.name;
			document.getElementById("ve-info-entity-foreign-keys-join-type").innerHTML = ve.refEntity.foreignKeys[0].joinType;
		} else {
			document.getElementById("ve-info-entity-foreign-keys-ref").innerHTML = "-";
			document.getElementById("ve-info-entity-foreign-keys-join-type").innerHTML = "-";
		}
		html = `
			<table>
			<thead>
				<tr>
					<th>参照先項目</th>
					<th>参照元項目</th>
					<th>固定値</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {
			ve.refEntity.foreignKeys[0].foreignKeyColumns.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.referenceColumn}</td>
						<td>${obj.sourceColumn}</td>
						<td>${obj.constValue}</td>
					</tr>`;
			});
		}
		
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-entity-foreign-keys-columns").innerHTML = html;
		if (ve.absoluteVirtualColumnCode == "-") {
			document.getElementById("copy-absolute-ve-btn").style.display = "none";
		} else {
			document.getElementById("copy-absolute-ve-btn").style.display = "block";
		}
		html = `
			<table>
			<thead>
				<tr>
					<th>仮想項目</th>
					<th>比較オペレーター</th>
					<th>フィルター値</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.filterConditions != undefined) {
			ve.filterConditions.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.column}</td>
						<td>${obj.comparisonOperator}</td>
						<td>${obj.filterValue}</td>
					</tr>`;
			});
		}
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-filter-conditions").innerHTML = html;
		
		html = `
			<table>
			<thead>
				<tr>
					<th>仮想項目</th>
					<th>比較オペレーター</th>
					<th>オプション</th>
					<th>スペース</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.searchConditions != undefined) {
			ve.searchConditions.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.column}</td>
						<td>${obj.comparisonOperator}</td>
						<td>${obj.optional ? 'Option' : 'Required [IS NULL]'}</td>
						<td>${obj.trim ? 'Trim' : 'No Delete'}</td>
					</tr>`;
			});
		}
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-search-conditions").innerHTML = html;
		
		html = `
			<table>
			<thead>
				<tr>
					<th>仮想項目</th>
					<th>タイプ</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.sortConditions) {
			ve.sortConditions.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.sortColumn}</td>
						<td>${obj.sortMode}</td>
					</tr>`;
			});
		}
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-sort-columns").innerHTML = html;
		
		html = `
			<table>
			<thead>
				<tr>
					<th>仮想項目</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.groupConditions != undefined) {
			ve.groupConditions.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.groupColumn}</td>
					</tr>`;
			});
		}
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-group-columns").innerHTML = html;
		
		if (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {
			ve.foreignKeys.forEach((obj, index) => {
				html += `<option>${obj.name}</option>`;
			});
		} else {
			html = `<option>-</option>`;
		}
		document.getElementById("ve-foreign-keys").innerHTML = html;
		document.getElementById("ve-foreign-keys").onchange = function() {
            switchForeignKey(objIndex, paginatedArray, (document.getElementById("ve-foreign-keys") as HTMLSelectElement).value)
		}
		if (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {
			document.getElementById("ve-info-foreign-keys-ref").innerHTML = ve.foreignKeys[0].referenceEntity.name;
			document.getElementById("ve-info-foreign-keys-join-type").innerHTML = ve.foreignKeys[0].joinType;
		} else {
			document.getElementById("ve-info-foreign-keys-ref").innerHTML = "-";
			document.getElementById("ve-info-foreign-keys-join-type").innerHTML = "-";
		}
		html = `
			<table>
			<thead>
				<tr>
					<th>参照先項目</th>
					<th>参照元項目</th>
					<th>固定値</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {
			ve.foreignKeys[0].foreignKeyColumns.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.referenceColumn}</td>
						<td>${obj.sourceColumn}</td>
						<td>${obj.constValue}</td>
					</tr>`;
			});
		}
		
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-foreign-keys-columns").innerHTML = html;
		checkAllTableContainerOverflow();
	}

	const switchRefEntityUniqueKey = (objIndex, paginatedArray, name) => {
		const ve = paginatedArray[objIndex].virtualEntity;
		let html = `
			<table>
			<thead>
				<tr>
					<th>項目名</th>
					<th>データタイプ</th>
					<th>サイズ</th>
					<th>少数サイズ</th>
					<th>Null可</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {
			const filteredUK = ve.refEntity.uniqueKeys.filter(uk => uk.name === name)[0];
			filteredUK.uniqueKeyColumns.forEach((obj, index) => {
				const isPrimaryKey = obj.primaryKey == true ? '<span className="absolute-ve">PK</span>' : '-';
				html += `<tr>
						<td>${obj.name}</td>
						<td>${obj.dataType}</td>
						<td>${obj.size}</td>
						<td>${obj.decimalSize}</td>
						<td>${obj.notNull}</td>
					</tr>`;
			});
		}
		
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-entity-unique-keys-columns").innerHTML = html;
	}


	const switchRefEntityForeignKey = (objIndex, paginatedArray, name) => {
		const ve = paginatedArray[objIndex].virtualEntity;
		let html = `
			<table>
			<thead>
				<tr>
					<th>参照先項目</th>
					<th>参照元項目</th>
					<th>固定値</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {
			const filteredFK = ve.refEntity.foreignKeys.filter(fk => fk.name === name)[0];
			document.getElementById("ve-info-entity-foreign-keys-ref").innerHTML = filteredFK.referenceEntity.name;
			document.getElementById("ve-info-entity-foreign-keys-join-type").innerHTML = filteredFK.joinType;
			filteredFK.foreignKeyColumns.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.referenceColumn}</td>
						<td>${obj.sourceColumn}</td>
						<td>${obj.constValue}</td>
					</tr>`;
			});
		}
		
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-entity-foreign-keys-columns").innerHTML = html;
	}


	const switchForeignKey = (objIndex, paginatedArray, name) => {
		const ve = paginatedArray[objIndex].virtualEntity;
		let html = `
			<table>
			<thead>
				<tr>
					<th>参照先項目</th>
					<th>参照元項目</th>
					<th>固定値</th>
				</tr>
			</thead>
			<tbody>`;
		if (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {
			const filteredFK = ve.foreignKeys.filter(fk => fk.name === name)[0];
			document.getElementById("ve-info-foreign-keys-ref").innerHTML = filteredFK.referenceEntity.name;
			document.getElementById("ve-info-foreign-keys-join-type").innerHTML = filteredFK.joinType;
			filteredFK.foreignKeyColumns.forEach((obj, index) => {
				html += `<tr>
						<td>${obj.referenceColumn}</td>
						<td>${obj.sourceColumn}</td>
						<td>${obj.constValue}</td>
					</tr>`;
			});
		}
		
		html += `
			</tbody>
		</table>`;
		document.getElementById("ve-info-foreign-keys-columns").innerHTML = html;
	}

    const changeDetailButtonIfAbsoluteColumnExist = (html, index, paginatedArray) => {
        const btn = document.createElement('button');
        if (paginatedArray[index].virtualEntity) {
            const absoluteCode = paginatedArray[index].virtualEntity.absoluteVirtualColumnCode;
            btn.id = "btn-details-"+index;
            if (absoluteCode != "-") {
                btn.className = 'details-btn tooltip';
                btn.innerHTML = `<span class="tooltiptext">完全仮想項目含む</span>
                        <div style="display:flex; justify-content:center; align-items:center; text-align:center; ">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-exclamation-circle" viewBox="0 0 16 16" style="margin-right:2px;">
                                <path d="M8 1a7 7 0 1 0 7 7 7 7 0 0 0-7-7zM7.002 4a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7 6h2v5H7z"/>
                            </svg>
                        詳細</div>`;
            } else {
            btn.className = 'details-btn';
                btn.innerHTML = `詳細`;
            }
        }
		
		html = html.replace("{detail_btn}", btn.outerHTML);
		return html;
	}
    const [applyTarget, setApplyTarget] = React.useState<{ id: string, type: string }>({ id: '', type: '' });
    const apply = async () => {
        showLoading();
        const selectedSetting = {
            id: "",
            savedName: "",
            savedTime: "",
            db: setting.db,
            reservedWords: {
                primaryKey: primaryKey,
                exclusiveKey: exclusiveKey,
                companyCodeKey: companyCodeKey,
                insertedUserIDKey: insertedUserIDKey,
                insertedDateKey: insertedDateKey,
                updatedUserIDKey: updatedUserIDKey,
                updatedDateKey: updatedDateKey
            },
            locale: {
              current: locale,
              mapping: {
                country1: country1,
                country2: country2,
                country3: country3,
                country4: country4,
                country5: country5
              }
            },
            transform: {
              encoding: transformEncoding,
              forceAliasColumn: forceAliasColumn,
              entityQuery: entityQuery,
              useForeignKey: useForeignKey,
              useExpMap: useExpMap
            },
        };
        const result = [...jsonArray];
        try {
            const sqls: SqlExtractionOutput = await extractSql(
                {
                    veId: applyTarget.id,
                    crudTypes: [applyTarget.type]
                },
                selectedSetting
            ); 
            const apiCodes = await convertSql2Code(sqls, selectedSetting);
                result.filter((item, index) => {
                    if (item.description === apiCodes.description && item.type === apiCodes.type) { 
                        result[index] = {
                            ...apiCodes,
                            id: applyTarget.id
                        };
                    }
                });

                setJsonArray(Array.from(result.values()).map((value) => value));
        } catch (error) {
            console.log(error)
        }
        setPrimaryKey(setting.reservedWords.primaryKey);
        setExclusiveKey(setting.reservedWords.exclusiveKey);
        setCompanyCodeKey(setting.reservedWords.companyCodeKey);
        setInsertedUserIDKey(setting.reservedWords.insertedUserIDKey);
        setInsertedDateKey(setting.reservedWords.insertedDateKey);
        setUpdatedUserIDKey(setting.reservedWords.updatedUserIDKey);
        setUpdatedDateKey(setting.reservedWords.updatedDateKey);
        setLocale(setting.locale.current);
        setCountry1(setting.locale.mapping.country1);
        setCountry2(setting.locale.mapping.country2);
        setCountry3(setting.locale.mapping.country3);
        setCountry4(setting.locale.mapping.country4);
        setCountry5(setting.locale.mapping.country5);
        setTransformEncoding(setting.transform.encoding);
        setForceAliasColumn(setting.transform.forceAliasColumn);
        setEntityQuery(setting.transform.entityQuery);
        setUseForeignKey(setting.transform.useForeignKey);
        setUseExpMap(setting.transform.useExpMap);
        hideLoading();
    }
    useEffect(()=> {
        
    })
    return {
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
        finalArray,
        setFinalArray,
        jsonArray,
        setJsonArray,
        isConverted,
        setIsConverted,
        processBarLabel,
        setProcessBarLabel,
        openSettingDialog,
        setOpenSettingDialog,
        isCompleted,
        setIsComplete,
        isConvertButtonDisabled,
        processBar,
        setProcessBar,
        applyTarget,
        setApplyTarget,
        setting,
        displayJSON,
        replaceApiCode,
        replaceToCamelCase,
        toggleDetails,
        copyToClipboard,
        showNotification,
        showLoading,
        hideLoading,
        handleOpenSettingDialog,
        downloadFile,
        searchJSON,
        changePaginationRange,
        displayPagination,
        displayPageInfo,
        changePage,
        hideWhere,
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
        onSearch,
    };
}

export default useTransformDialogData;