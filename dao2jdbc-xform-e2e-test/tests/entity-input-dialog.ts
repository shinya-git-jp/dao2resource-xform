import { Locator, Page } from "@playwright/test";

export class InputDialogPage {
    readonly page: Page;
    readonly closeButton: Locator;
    readonly saveButton: Locator;
    readonly checkButton: Locator;
    readonly idField: Locator;
    readonly databaseIdRadioButton: Locator;
    readonly entityIdRadioButton: Locator;
    readonly additionalInfoExtendedLabel: Locator;
    readonly settingInfoExtendedLabel: Locator;
    readonly settingOptions: Locator;
    

    constructor(page: Page) {
        this.page = page;
        this.closeButton = page.getByRole('button', { name: 'close' });
        this.saveButton = page.getByRole('button', { name: '保存' });
        this.checkButton = page.getByRole('button', { name: 'IDの検証' });
        this.idField = page.getByRole('textbox', { name: 'ID' });
        this.databaseIdRadioButton = page.getByRole('radio', { name: 'データベースID' });
        this.entityIdRadioButton = page.getByRole('radio', { name: 'エンティティID' });
        this.additionalInfoExtendedLabel = page.locator('div').filter({ hasText: '追加情報' }).nth(3);
        this.settingInfoExtendedLabel = page.getByRole('treeitem', { name: '設定情報' }).locator('div').first();
        this.settingOptions = page.getByTestId('selected-setting');
    }

    async clickCloseButton() {
        await this.closeButton.click();
    }
    async clickSaveButton() {
        await this.saveButton.click();
    }
    async clickCheckButton() {
        await this.checkButton.click();
    }
    async setId(id: string) {
        await this.idField.fill(id);
    }
    async clickAdditionalInfoExtendedLabel() {
        await this.additionalInfoExtendedLabel.click();
    }
    async clickSettingInfoExtendedLabel() {
        await this.settingInfoExtendedLabel.click();
    }
    async clickDatabaseIdRadioButton() {
        await this.databaseIdRadioButton.click();
    }
    async clickEntityIdRadioButton() {
        await this.entityIdRadioButton.click();
    }
    async selectSettingOptions(name: string) {
        await this.settingOptions.click();
        await this.page.getByRole('option', { name: name, exact: true }).click()
    }

}