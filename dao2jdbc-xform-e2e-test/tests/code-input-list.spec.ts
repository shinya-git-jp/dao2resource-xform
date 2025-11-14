import { test, expect, Page } from '@playwright/test';
import { DEFAULT_URL } from './constants';
import { InputListPage } from './code-input-list';
import { SettingListPage } from './setting-list';
import * as fs from 'fs';

test.describe.configure({mode: "serial"});
test.slow();
test.beforeEach(async ({page})=> {
    await page.goto(DEFAULT_URL);
})
const addFalseSetting  = async (page: Page) => {
    
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe2");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkForceAliasColumn();
    await settingListPage.settingDialog.checkUseExpMap();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("test");
    await settingListPage.settingDialog.clickCloseButton();
}
const addSetting = async (page: Page) => {
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkForceAliasColumn();
    await settingListPage.settingDialog.checkUseExpMap();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("test");
    await settingListPage.settingDialog.clickCloseButton();
}

const addSettingWithoutForceAliasColumn = async (page: Page) => {
    const settingListPage = new SettingListPage(page);
    await settingListPage.openSettingPage();
    await settingListPage.clickNewItemButton();
    await settingListPage.settingDialog.selectDatabaseSettingsSection();
    await settingListPage.settingDialog.setUrlField("jdbc:mysql://gef-unit-mysql8027.c3cxjzdf21mz.ap-northeast-1.rds.amazonaws.com:3306/gef_jdbc_tool?zeroDateTimeBehavior=convertToNull&useUnicode=true&useSSL=false&serverTimezone=GMT%2B9");
    await settingListPage.settingDialog.setDbTypeField("MySQL");
    await settingListPage.settingDialog.setSchemaField("gef_jdbc_tool");
    await settingListPage.settingDialog.setUsernameField("geframe");
    await settingListPage.settingDialog.setPasswordField("Kccs0000");
    await settingListPage.settingDialog.selectReservedWordsSettingsSection();
    await settingListPage.settingDialog.setPrimaryKeyField("objectID");
    await settingListPage.settingDialog.setExclusiveKeyField("ExclusiveFG");
    await settingListPage.settingDialog.setCompanyCodeField("CompanyCD");
    await settingListPage.settingDialog.setInsertedUserIdField("RegisteredPerson");
    await settingListPage.settingDialog.setInsertedDateField("RegisteredDT");
    await settingListPage.settingDialog.setUpdatedUserIdField("UpdatedPerson");
    await settingListPage.settingDialog.setUpdatedDateField("UpdatedDT");
    await settingListPage.settingDialog.selectLocaleSettingsSection();
    await settingListPage.settingDialog.setLocaleField("en");
    await settingListPage.settingDialog.setCountry1Field("en");
    await settingListPage.settingDialog.setCountry2Field("jp");
    await settingListPage.settingDialog.setCountry3Field("id");
    await settingListPage.settingDialog.setCountry4Field("zh");
    await settingListPage.settingDialog.setCountry5Field("us");
    await settingListPage.settingDialog.selectTransformSettingsSection();
    await settingListPage.settingDialog.checkUseForeignKey();
    await settingListPage.settingDialog.clickSaveButton("settingWithoutForceAliasColumn");
    await settingListPage.settingDialog.clickCloseButton();
}

const addNewInputVeId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewFalseInputVeId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32Z');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewInputCategoryId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('F7C28107728F40BAB689C22CD75CDA26');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}
const addNewFalseInputCategoryId = async (inputListPage: InputListPage) => {
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('F7C28107728F40BAB689C22CD75CDA2Z');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
}

test('存在する仮想表IDを入力し、「IDの検証」ボタンで検証すると、「IDが存在する」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在します。');
    
});

test('存在しない仮想表IDを入力し、「IDの検証」ボタンで検証すると、「IDが存在しません」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32Z');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});

test('存在しない仮想表IDを入力し、「保存」ボタンで検証すると、「IDが存在しません。保存できません。」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32A');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickSaveButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません。保存できません。');
});

test('存在するカテゴリIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在する」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('F56264D123E6450DB2AF788A7039CA41');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在します。');
});

test('存在しないカテゴリIDを入力し、「IDの検証」ボタンで検証すると、「IDが存在しません」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('F7C28107728F40BAB689C22CD75CDA2Z');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});

test('存在しないカテゴリIDを入力し、「保存」ボタンで検証すると、「IDが存在しません。保存できません。」と表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32A');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickSaveButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません。保存できません。');
});

test('グローバル設定に接続できない場合、「IDの検証」ボタンで検証すると「IDが存在しません」と表示されること。', async ({ page }) => {
    await addFalseSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('F7C28107728F40BAB689C22CD75CDA2Z');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickCategoryIdRadioButton();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        await dialog.accept();
    });
    await inputListPage.inputDialog.clickCheckButton();
    await expect(page.getByText("確認中")).toBeVisible();
    await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
    expect(alertMessage).toBe('IDが存在しません');
});

test("変換条件を削除した後、ページを再読み込みしても、記録から削除された状態が保持されていること。", async({page}) => {
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectTransformConditionSection();
    await addNewInputVeId(inputListPage);
    await inputListPage.loadListInputs();
    let listInputs = await inputListPage.listInputs.count();
    let listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickListInputsCheckbox(0)
    await inputListPage.clickDeleteButton();
    listInputs = await inputListPage.listInputs.count();
    listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(0);
    expect(listInputsCheckboxes).toBe(0);
})

// test('特定設定が接続できない場合、「チェック」ボタンで検証すると「IDが存在しません」が表示されること。', async ({ page }) => {
//     await addFalseSetting(page);
//     await addSettingWithoutForceAliasColumn(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openCodeInputPage();
//     await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
    
//     await inputListPage.selectTransformConditionSection();
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('F7C28107728F40BAB689C22CD75CDA2Z');
//     await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
//     await inputListPage.inputDialog.clickCategoryIdRadioButton();
//     await inputListPage.inputDialog.selectSettingOptions('test');
    
//     let alertMessage = '';
//     page.on('dialog', async (dialog) => {
//         alertMessage = dialog.message();
//         await dialog.accept();
//     });
//     await inputListPage.inputDialog.clickCheckButton();
//     await expect(page.getByText("確認中")).toBeVisible();
//     await expect(page.getByText("確認中")).toBeHidden({timeout: 1200000});
//     expect(alertMessage).toBe('IDが存在しません。');
// });

test('forceAliasColumnフラグとexpMapフラグが共にtrueの場合、変換後の結果にエイリアスとexpMapが適用されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await addNewInputVeId(inputListPage);
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / (SELECT)")).toBeVisible({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton();

    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickScript("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT");
    await expect(page.getByText("SELECT SError.objectID, SError.errorNo, SError.errorMessageID, SError.errorType, SError.description, SLocalization.country1, SLocalization.country2, SLocalization.country3, SLocalization.country4, SLocalization.country5 FROM gef_jdbc_tool.SError SError INNER JOIN gef_jdbc_tool.SLocalization SLocalization ON SError.errorMessageID = SLocalization.objectID WHERE SError.errorNo = ? ORDER BY SError.errorNo ASC").first()).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); List result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findList();`);
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findList();`);
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); GRecord result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findRecord();`);
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecord();`);
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); GRecordSet result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findRecordSet();`);
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecordSet();`);
    
});

test('forceAliasColumnフラグとexpMapフラグが共にfalseの場合、変換後の結果にエイリアスとexpMapが適用されないこと。', async ({ page }) => {
    await addSettingWithoutForceAliasColumn(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
    await inputListPage.selectTransformConditionSection();
    await addNewInputVeId(inputListPage);
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / (SELECT)")).toBeVisible({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton();

    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickScript("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT");
    await expect(page.getByText("SELECT SError.objectID, SError.errorNo, SError.errorMessageID, SError.errorType, SError.description, SLocalization.country1, SLocalization.country2, SLocalization.country3, SLocalization.country4, SLocalization.country5 FROM gef_jdbc_tool.SError SError INNER JOIN gef_jdbc_tool.SLocalization SLocalization ON SError.errorMessageID = SLocalization.objectID WHERE SError.errorNo = ? ORDER BY SError.errorNo ASC").first()).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findList();`);
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findList();`);
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecord();`);
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecord();`);
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecordSet();`);
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecordSet();`);
});

// test('入力されたスクリプトに、単体設定が設定される場合、変換された結果が単体設定通りに処理されること。', async ({ page }) => {
//     await addSetting(page);
//     await addSettingWithoutForceAliasColumn(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openCodeInputPage();
//     await inputListPage.selectGlobalSetting('test');
//     await inputListPage.selectTransformConditionSection();
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
//     await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
//     await inputListPage.inputDialog.clickVeIdRadioButton();
//     await inputListPage.inputDialog.clickSettingInfoExtendedLabel();
//     await inputListPage.inputDialog.selectSettingOptions('settingWithoutForceAliasColumn');
//     await inputListPage.inputDialog.clickSaveButton();
//     await inputListPage.inputDialog.clickCloseButton();
//     await inputListPage.loadListInputs();
//     const listInputs = await inputListPage.listInputs.count();
//     const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
//     expect(listInputs).toBe(1);
//     expect(listInputsCheckboxes).toBe(1);
//     await inputListPage.clickTransformScriptButton();
//     await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / (SELECT)")).toBeVisible({timeout: 1200000});
//     await inputListPage.transformDialog.clickTransformButton();

//     await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT")).toBeVisible({timeout: 1200000});
    
//     await inputListPage.transformDialog.clickScript("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT");
//     await expect(page.getByText("SELECT SError.objectID, SError.errorNo, SError.errorMessageID, SError.errorType, SError.description, SLocalization.country1, SLocalization.country2, SLocalization.country3, SLocalization.country4, SLocalization.country5 FROM gef_jdbc_tool.SError SError INNER JOIN gef_jdbc_tool.SLocalization SLocalization ON SError.errorMessageID = SLocalization.objectID WHERE SError.errorNo = ? ORDER BY SError.errorNo ASC").first()).toBeVisible();
//     await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toBeVisible();
//     await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findList();`);
//     await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toBeVisible();
//     await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findList();`);
//     await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toBeVisible();
//     await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecord();`);
//     await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toBeVisible();
//     await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecord();`);
//     await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
//     await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecordSet();`);
//     await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
//     await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecordSet();`);
// });

test("変換された結果に、設定を再適用されると、変換された結果が再取得されること。", async ({ page }) => {
    await addSettingWithoutForceAliasColumn(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / (SELECT)")).toBeVisible({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton();

    await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT")).toBeVisible({timeout: 1200000});
    
    await inputListPage.transformDialog.clickScript("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT");
    await expect(page.getByText("SELECT SError.objectID, SError.errorNo, SError.errorMessageID, SError.errorType, SError.description, SLocalization.country1, SLocalization.country2, SLocalization.country3, SLocalization.country4, SLocalization.country5 FROM gef_jdbc_tool.SError SError INNER JOIN gef_jdbc_tool.SLocalization SLocalization ON SError.errorMessageID = SLocalization.objectID WHERE SError.errorNo = ? ORDER BY SError.errorNo ASC").first()).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findList();`);
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findList();`);
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecord();`);
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecord();`);
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .where(exp($(\"SError.errorNo = [?]\"), whereParams)) .findRecordSet();`);
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\"),col(\"SError.errorNo\"),col(\"SError.errorMessageID\"),col(\"SError.errorType\"),col(\"SError.description\"),col(\"SLocalization.country1\"),col(\"SLocalization.country2\"),col(\"SLocalization.country3\"),col(\"SLocalization.country4\"),col(\"SLocalization.country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecordSet();`);
    await page.getByRole('button', { name: '設定' }).click();
    await inputListPage.transformDialog.settingDialog.clickTransformExtendedLabel();
    await inputListPage.transformDialog.settingDialog.checkUseExpMap();
    await inputListPage.transformDialog.settingDialog.checkForceAliasColumn();
    await inputListPage.transformDialog.settingDialog.clickSaveButton();

    await expect(page.getByText('変換中(1 件の変換は完了しました)。。。')).toBeVisible({timeout: 1200000});
    await expect(page.getByText('変換中(1 件の変換は完了しました)。。。')).not.toBeVisible({timeout: 1200000});
    await expect(page.getByText("SELECT SError.objectID, SError.errorNo, SError.errorMessageID, SError.errorType, SError.description, SLocalization.country1, SLocalization.country2, SLocalization.country3, SLocalization.country4, SLocalization.country5 FROM gef_jdbc_tool.SError SError INNER JOIN gef_jdbc_tool.SLocalization SLocalization ON SError.errorMessageID = SLocalization.objectID WHERE SError.errorNo = ? ORDER BY SError.errorNo ASC").first()).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-1-value-findList pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); List result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findList();`);
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-1-value-findList pre')).toHaveText(`Object[] whereParams = new Object[]{}; List result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findList();`);
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-2-value-findRecord pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); GRecord result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findRecord();`);
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-2-value-findRecord pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecord result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecord();`);
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#where-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Map whereParams = new HashMap<>(); whereParams.put("SError.errorNo",); GRecordSet result = select("SError", "SError") .fields(col("SError.objectID", "ObjectID"),col("SError.errorNo", "ErrorNo"),col("SError.errorMessageID", "ErrorMessageID"),col("SError.errorType", "ErrorType"),col("SError.description", "Description"),col("SLocalization.country1", "Country1"),col("SLocalization.country2", "Country2"),col("SLocalization.country3", "Country3"),col("SLocalization.country4", "Country4"),col("SLocalization.country5", "Country5")) .innerJoinFK("FK1", "SLocalization") .orderBy(asc("SError.errorNo")) .where(expMap($("SError.errorNo = [:SError.errorNo]"), whereParams)) .findRecordSet();`);
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toBeVisible();
    await expect(page.locator('#wherePK-0-SELECT-3-value-findRecordSet pre')).toHaveText(`Object[] whereParams = new Object[]{}; GRecordSet result = select(\"SError\", \"SError\") .fields(col(\"SError.objectID\", \"ObjectID\"),col(\"SError.errorNo\", \"ErrorNo\"),col(\"SError.errorMessageID\", \"ErrorMessageID\"),col(\"SError.errorType\", \"ErrorType\"),col(\"SError.description\", \"Description\"),col(\"SLocalization.country1\", \"Country1\"),col(\"SLocalization.country2\", \"Country2\"),col(\"SLocalization.country3\", \"Country3\"),col(\"SLocalization.country4\", \"Country4\"),col(\"SLocalization.country5\", \"Country5\")) .innerJoinFK(\"FK1\", \"SLocalization\") .orderBy(asc(\"SError.errorNo\")) .wherePK(whereParams) .findRecordSet();`);
    
});


// test('グローバル設定に接続できない場合、変換が続行できず、「選択された変換設定で接続が失敗です。」エラーメッセージが表示されること。', async ({ page }) => {
//     await addFalseSetting(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openCodeInputPage();
//     await inputListPage.selectGlobalSetting('test');
//     await inputListPage.selectTransformConditionSection();
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
//     await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
//     await inputListPage.inputDialog.clickVeIdRadioButton();
//     await inputListPage.inputDialog.clickSaveButton();
//     await inputListPage.inputDialog.clickCloseButton();
//     await inputListPage.loadListInputs();
//     const listInputs = await inputListPage.listInputs.count();
//     const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
//     expect(listInputs).toBe(1);
//     expect(listInputsCheckboxes).toBe(1);
//     let alertMessage = '';
//     page.on('dialog', async (dialog) => {
//         alertMessage = dialog.message();
//         await dialog.accept();
//         expect(alertMessage).toBe('選択された変換設定で接続が失敗です。');
//     });
    
//     await inputListPage.clickTransformScriptButton();
// });

// test('特定設定が接続できない場合、変換された結果が空になること。', async ({ page }) => {
//     await addFalseSetting(page);
//     await addSettingWithoutForceAliasColumn(page);
//     const inputListPage = new InputListPage(page);
//     await inputListPage.openCodeInputPage();
//     await inputListPage.selectGlobalSetting('settingWithoutForceAliasColumn');
//     await inputListPage.clickNewItemButton();
//     await inputListPage.inputDialog.setId('0AF1C308DE0848E9AB8FFD11D4EEC32E');
//     await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
//     await inputListPage.inputDialog.clickVeIdRadioButton();
//     await inputListPage.inputDialog.clickSettingInfoExtendedLabel();
//     await inputListPage.inputDialog.selectSettingOptions('test');
//     await inputListPage.inputDialog.clickSaveButton();
//     await inputListPage.inputDialog.clickCloseButton();
//     await inputListPage.loadListInputs();
//     const listInputs = await inputListPage.listInputs.count();
//     const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
//     expect(listInputs).toBe(1);
//     expect(listInputsCheckboxes).toBe(1);
//     await inputListPage.clickTransformScriptButton();
//     await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / (SELECT)")).toBeVisible({timeout: 1200000});
//     await inputListPage.transformDialog.clickTransformButton();
//     await expect(page.getByText("変換中。。。").first()).not.toBeVisible({timeout: 1200000});
//     await expect(page.getByText("仮想表ID: 0AF1C308DE0848E9AB8FFD11D4EEC32E / SELECT")).not.toBeVisible({timeout: 1200000});
// });

test('入力された仮想表のエンティティに全てのプロパティが設定されている場合、変換後の結果が正しいこと。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.clickNewItemButton();
    await inputListPage.inputDialog.setId('35B6AB911C524EE69A30E29BF9A2904A');
    await inputListPage.inputDialog.checkSelectCrudTypeCheckbox();
    await inputListPage.inputDialog.clickVeIdRadioButton();
    await inputListPage.inputDialog.clickSaveButton();
    await inputListPage.inputDialog.clickCloseButton();
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(1);
    expect(listInputsCheckboxes).toBe(1);
    await inputListPage.clickTransformScriptButton();
    await expect(page.getByText("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A / (SELECT)")).toBeVisible({timeout: 1200000});
    await inputListPage.transformDialog.clickTransformButton(); 
    await expect(page.getByText("仮想表ID: 35B6AB911C524EE69A30E29BF9A2904A / SELECT")).toBeVisible({timeout: 1200000});
    await expect(page.locator('#btn-details-0')).toMatchAriaSnapshot(`
    - img
    - text: 詳細
    `);
    await page.locator('#btn-details-0').click();
    await expect(page.locator('#ve-info-foreign-keys-ref')).toContainText('jdbc_tool_test_fullCaseRightJoinEntity');
    await expect(page.locator('#ve-info-foreign-keys-join-type')).toContainText('RIGHT_OUTER');
    await expect(page.locator('#ve-info-foreign-keys-columns')).toContainText('NStringColumnRight');
    await expect(page.locator('#ve-info-foreign-keys-columns')).toContainText('StringColumnRight');
    await page.locator('#ve-foreign-keys').selectOption('LeftJoinRefColumnAndConstValue');
    await expect(page.locator('#ve-info-foreign-keys-ref')).toContainText('jdbc_tool_test_fullCaseLeftJoinEntity');
    await expect(page.locator('#ve-info-foreign-keys-join-type')).toContainText('LEFT_OUTER');
    await expect(page.locator('#ve-info-foreign-keys-columns')).toContainText('IntColumnLeft');
    await expect(page.locator('#ve-info-foreign-keys-columns')).toContainText('LongColumnLeft');
    await expect(page.locator('#ve-info-columns')).toContainText('完全仮想項目');
    await expect(page.locator('#copy-absolute-ve-btn')).toContainText('完全仮想項目をコピー');
    await page.locator('#copy-absolute-ve-btn').click();
    await expect(page.locator('#notification')).toContainText('Copied to clipboard!');

    await page.getByText('メインエンティティ', { exact: true }).click();
    await page.locator('#ve-entity-unique-keys').selectOption('SingleColumnUK');
    await expect(page.locator('#ve-entity-unique-keys')).toHaveValue('SingleColumnUK');
    await expect(page.locator('#ve-info-entity-unique-keys-columns')).toContainText('StringColumn');
    await page.locator('#ve-entity-unique-keys').selectOption('MultipleColumnUK');
    await expect(page.locator('#ve-info-entity-unique-keys-columns')).toContainText('StringColumn');
    await expect(page.locator('#ve-info-entity-unique-keys-columns')).toContainText('IntColumn');
    await expect(page.locator('#ve-info-entity-unique-keys-columns')).toContainText('NStringColumn');

    await expect(page.locator('#ve-entity-foreign-keys')).toHaveValue('InnerJoinRefCoulumnAndConstValue');
    await expect(page.locator('#ve-info-entity-foreign-keys-ref')).toContainText('jdbc_tool_test_fullCaseInnerJoinEntity');
    await expect(page.locator('#ve-info-entity-foreign-keys-join-type')).toContainText('INNER');
    await expect(page.locator('#ve-info-entity-foreign-keys-columns')).toContainText('StringColumnInner');
    await expect(page.locator('#ve-info-entity-foreign-keys-columns')).toContainText('NStringColumnInner');
    await page.locator('#ve-entity-foreign-keys').selectOption('RightJoinRefColumnAndConstValue');
    await expect(page.locator('#ve-info-entity-foreign-keys-ref')).toContainText('jdbc_tool_test_fullCaseRightJoinEntity');
    await expect(page.locator('#ve-info-entity-foreign-keys-join-type')).toContainText('RIGHT_OUTER');
    await expect(page.locator('#ve-info-entity-foreign-keys-columns')).toContainText('NStringColumnRight');
    await expect(page.locator('#ve-info-entity-foreign-keys-columns')).toContainText('StringColumnRight');
    
});


test('インポートファイルの形式が一致する場合、インポートが成功し、インポートされた条件が適切に表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    await inputListPage.selectImportFileTemp('test-files/dao2-code-xform(input).yml');
    await inputListPage.loadListInputs();
    const listInputs = await inputListPage.listInputs.count();
    const listInputsCheckboxes = await inputListPage.listInputsCheckboxes.count();
    expect(listInputs).toBe(3);
    expect(listInputsCheckboxes).toBe(3);
});

test('インポートファイルの形式が一致しない場合、インポートが失敗し、「エラー」が表示されること。', async ({ page }) => {
    await addSetting(page);
    const inputListPage = new InputListPage(page);
    await inputListPage.openCodeInputPage();
    await inputListPage.selectGlobalSetting('test');
    await inputListPage.selectTransformConditionSection();
    
    let alertMessage = '';
    page.on('dialog', async (dialog) => {
        alertMessage = dialog.message();
        expect(alertMessage).toBe('フォーマットは違います。');
    });
    await inputListPage.selectImportFileTemp('test-files/dao2-code-xform(input)-false.yml');
    
});