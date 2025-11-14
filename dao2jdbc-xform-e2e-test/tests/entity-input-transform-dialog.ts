import { Locator, Page } from "@playwright/test";

export class TransformedScript {
    readonly page: Page;
    readonly description: string;
    readonly settingButton: Locator;
    
    constructor(page: Page, description: string) {
        this.page = page;
        this.description = description;
        this.settingButton = page.getByRole('button', { name: '設定' });
    }
}



export class SettingDialogPage {
    readonly page: Page;
    readonly closeButton: Locator;
    readonly saveButton: Locator;
    readonly importButton: Locator;
    readonly importFileTemp: Locator;
    readonly saveNameField: Locator;
    readonly conditionEncodingField: Locator;
    readonly transformEncodingField: Locator;
    readonly queryModeSelectField: Locator;
    readonly forceAliasColumnCheckBox: Locator;
    readonly useExpMapCheckbox: Locator;
    readonly useForeignKeyCheckbox: Locator;
    readonly localeInfoExtendedLabel: Locator;
    readonly reservedWordsExtendedLabel: Locator;
    readonly conditionInfoExtendedLabel: Locator;
    readonly transformExtendedLabel: Locator;

    constructor(page: Page) {
        this.page = page;
        this.closeButton = page.getByRole('button', { name: 'close' });
        this.saveButton = page.getByRole('button', { name: '適用' });
        this.importButton = page.getByRole('button', { name: 'インポート' });
        this.saveNameField = page.getByRole('textbox', { name: '保存名' });
        this.conditionEncodingField = page.getByRole('textbox', { name: 'Conditionのエンコーディング' });
        this.transformEncodingField = page.getByRole('textbox', { name: 'Transformのエンコーディング' });
        this.queryModeSelectField = page.getByText('ENTITY_QUERY').locator('..').getByRole('combobox', { name: 'クエリモード' });
        this.forceAliasColumnCheckBox = page.getByRole('checkbox', { name: 'エイリアスカラムを強制する' });
        this.useExpMapCheckbox = page.getByRole('checkbox', { name: 'ExpMapを使用する' });
        this.useForeignKeyCheckbox = page.getByRole('checkbox', { name: '外部キーを使用する' });
        this.importFileTemp = page.getByTestId('fileInputSettingDialog');
        this.localeInfoExtendedLabel = page.getByText("言語情報");
        this.reservedWordsExtendedLabel = page.getByText("予約言語設定");
        this.conditionInfoExtendedLabel = page.getByText("インプット情報");
        this.transformExtendedLabel = page.getByText("アウトプット情報");
    }

    async clickCloseButton() {
        await this.closeButton.click();
    }
    async clickSaveButton() {
        await this.saveButton.click();
    }
    async clickImportButton() {
        await this.importButton.click();
    }
    async selectImportFileTemp(filePath: string) {
        await this.importFileTemp.setInputFiles(filePath);
    }
    async setSaveName(name: string) {
        await this.saveNameField.fill(name);
    }
    async setConditionEncoding(encoding: string) {
        await this.conditionEncodingField.fill(encoding);
    }
    async setTransformEncoding(encoding: string) {
        await this.transformEncodingField.fill(encoding);
    }
    async selectQueryMode(mode: string) {
        await this.queryModeSelectField.selectOption(mode);
    }
    async checkForceAliasColumn() {
        await this.forceAliasColumnCheckBox.check();
    }
    async uncheckForceAliasColumn() {
        await this.forceAliasColumnCheckBox.uncheck();
    }
    async checkUseExpMap() {
        await this.useExpMapCheckbox.check();
    }
    async uncheckUseExpMap() {
        await this.useExpMapCheckbox.uncheck();
    }
    async checkUseForeignKey() {
        await this.useForeignKeyCheckbox.check();
    }
    async uncheckUseForeignKey() {
        await this.useForeignKeyCheckbox.uncheck();
    }
    async isSaveButtonEnabled() {
        return await this.saveButton.isEnabled();
    }
    async isImportButtonEnabled() {
        return await this.importButton.isEnabled();
    }
    async isCloseButtonEnabled() {
        return await this.closeButton.isEnabled();
    }
    async isSaveNameFieldEnabled() {
        return await this.saveNameField.isEnabled();
    }
    async isConditionEncodingFieldEnabled() {
        return await this.conditionEncodingField.isEnabled();
    }
    async isTransformEncodingFieldEnabled() {
        return await this.transformEncodingField.isEnabled();
    }
    async isQueryModeSelectFieldEnabled() {
        return await this.queryModeSelectField.isEnabled();
    }
    async isForceAliasColumnCheckBoxEnabled() {
        return await this.forceAliasColumnCheckBox.isEnabled();
    }
    async isUseExpMapCheckBoxEnabled() {
        return await this.useExpMapCheckbox.isEnabled();
    }
    async isUseForeignKeyCheckBoxEnabled() {
        return await this.useForeignKeyCheckbox.isEnabled();
    }
    async clickLocaleInfoExtendedLabel() {
        await this.localeInfoExtendedLabel.click();
    }
    async clickReservedWordsExtendedLabel() {
        await this.reservedWordsExtendedLabel.click();
    }
    async clickConditionInfoExtendedLabel() {
        await this.conditionInfoExtendedLabel.click();
    }
    async clickTransformExtendedLabel() {
        await this.transformExtendedLabel.click();
    }
}

export class InputTransformDialog {
    readonly page: Page;
    readonly searchField: Locator;
    readonly crudTypeCombobox: Locator;
    readonly paginationNumberCombobox: Locator;
    readonly nextPageButton: Locator;
    readonly previousPageButton: Locator;
    readonly downloadButton: Locator;
    readonly settingDialog: SettingDialogPage;
    readonly transformButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.searchField = page.getByRole('textbox', { name: 'デスクリプションで検索' });
        this.crudTypeCombobox = page.locator('#searchByType');
        this.paginationNumberCombobox = page.locator('#paginationNumber');
        this.nextPageButton = page.getByRole('button', { name: '次へ' });
        this.previousPageButton = page.getByRole('button', { name: '前へ' });
        this.downloadButton = page.getByRole('button', { name: 'add' });
        this.transformButton = page.getByRole('button', { name: 'add' });
        this.settingDialog = new SettingDialogPage(page);
    }

    async setSearchField(value: string) {
        await this.searchField.fill(value);
    }
    async selectCrudType(value: string) {
        await this.crudTypeCombobox.click();
        await this.page.getByRole('option', { name: value, exact: true }).click()
    }

    async selectPaginationNumber(value: string) {
        await this.paginationNumberCombobox.click();
        await this.page.getByRole('option', { name: value, exact: true }).click()
    }
    async clickNextPageButton() {
        await this.nextPageButton.click();
    }
    async clickPreviousPageButton() {
        await this.previousPageButton.click();
    }
    async clickTransformButton() {
        await this.transformButton.click();
    }
    async clickDownloadButton() {
        await this.downloadButton.click();
    }
    async clickScript(description: string) {
        const element = await this.page.waitForSelector(`text=${description}`, { timeout: 120000 });
        await element.click();
    }
    async clickSettingButton(index: number) {
        await this.page.getByRole('button', { name: '設定' }).nth(index).click();
    }
}