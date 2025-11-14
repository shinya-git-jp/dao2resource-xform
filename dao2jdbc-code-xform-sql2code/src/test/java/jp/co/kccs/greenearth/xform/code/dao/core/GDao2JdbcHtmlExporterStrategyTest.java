package jp.co.kccs.greenearth.xform.code.dao.core;

import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterForm;
import jp.co.kccs.greenearth.xform.code.jdbc.core.export.GExporterResult;
import jp.co.kccs.greenearth.xform.code.jdbc.core.service.*;
import org.junit.Test;
import java.util.List;

import static jp.co.kccs.greenearth.xform.code.dao.core.GUtilTest.getResult;
import static jp.co.kccs.greenearth.xform.code.dao.core.GUtilTest.getXFormCodeResult;
import static org.junit.Assert.*;

public class GDao2JdbcHtmlExporterStrategyTest {

	/**
	 * [1] {@link GExporterForm}がnullの場合、抽出されたファイルがnullになること。<br>
	 * [2] {@link GExporterForm}がnullじゃないの場合、抽出されたファイルがファイルになること。<br>
	 */
	@Test
	public void testExport() {
		GDao2JdbcHtmlExporterStrategy strategy = new GDao2JdbcHtmlExporterStrategy();
		List<GXFormCodeResult> codeResults = getXFormCodeResult();
		GExporterForm<List<GXFormCodeResult>> exporterForm = getResult(codeResults);

		{
			GExporterResult<List<GXFormCodeResult>, byte[]> exporterResult = strategy.export(exporterForm);
			assertEquals(5, exporterResult.getDataSize());
			assertEquals("html", exporterResult.getMediaType());
			String outputHtml = new String(exporterResult.getResult());
			assertEquals(codeResults, exporterResult.getData());
			assertEquals(getExpectedResult(), outputHtml);
		}
		{
			GExporterResult<List<GXFormCodeResult>, byte[]> exporterResult = strategy.export(null);
			assertNull(exporterResult);
		}
	}
	/**
	 * [1] メディアタイプが一致する場合、trueが返されること。<br>
	 * [2] メディアタイプが一致しないン場合、falseが返されること。<br>
	 * [3] データがnullの場合、falseが返されること。<br>
	 */
	@Test
	public void testCanExport() {
		GDao2JdbcHtmlExporterStrategy strategy = new GDao2JdbcHtmlExporterStrategy();
		GExporterForm<List<GXFormCodeResult>> exporterForm = getResult(getXFormCodeResult());
		{
			boolean result = strategy.canExport(exporterForm);
			assertTrue(result);
		}
		{
			exporterForm.setMediaType("csv");
			boolean result = strategy.canExport(exporterForm);
			assertFalse(result);
		}
		{
			exporterForm.setMediaType("html");
			exporterForm.setData(null);
			boolean result = strategy.canExport(exporterForm);
			assertFalse(result);
		}
	}


	private String getExpectedResult() {
		return "<!DOCTYPE html>\n" +
				"<html lang=\"en\">\n" +
				"<head>\n" +
				"    <meta charset=\"UTF-8\">\n" +
				"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
				"\t<link href=\"https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap\" rel=\"stylesheet\">\n" +
				"\n" +
				"    <title>XForm Code Report</title>\n" +
				"    <style>\n" +
				"        body {\n" +
				"            font-family: 'Poppins', sans-serif;\n" +
				"            background-color: #f0f0f0;\n" +
				"            margin: 0;\n" +
				"            padding: 20px;\n" +
				"            display: flex;\n" +
				"            flex-direction: column;\n" +
				"            align-items: center;\n" +
				"            position: relative;\n" +
				"        }\n" +
				"        #header {\n" +
				"            position: fixed;\n" +
				"            top: 0;\n" +
				"            left: 0;\n" +
				"            width: 100%;\n" +
				"            background-color: #fff;\n" +
				"            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
				"            z-index: 1000;\n" +
				"            padding: 5px 0;\n" +
				"            display: flex;\n" +
				"            flex-direction: column;\n" +
				"            align-items: center;\n" +
				"        }\n" +
				"        h1 {\n" +
				"\t\t\tfont-size: 2.5rem; \n" +
				"            text-align: center;\n" +
				"            font-size: 22px;\n" +
				"            font-weight: 700; \n" +
				"            color: #2c3e50;\n" +
				"            margin-bottom: 20px;\n" +
				"            letter-spacing: 1px;\n" +
				"            text-transform: uppercase;\n" +
				"        }\n" +
				"\n" +
				"        #searchContainer {\n" +
				"            width: 100%;\n" +
				"            max-width: 600px;\n" +
				"            display: flex;\n" +
				"            gap: 15px;\n" +
				"            margin-bottom: 20px;\n" +
				"        }\n" +
				"        #searchByDescription, #searchByType, #paginationNumber {\n" +
				"            padding: 10px 15px;\n" +
				"            border: 1px solid #bdc3c7;\n" +
				"            border-radius: 5px;\n" +
				"            font-size: 1rem;\n" +
				"        }\n" +
				"\t\tselect[id^=\"filterByApi-\"] {\n" +
				"            padding: 10px 5px;\n" +
				"            border: 1px solid #bdc3c7;\n" +
				"            border-radius: 5px;\n" +
				"            font-size: 1rem;\n" +
				"\t\t}\n" +
				"\n" +
				"        #searchByDescription {\n" +
				"            flex: 2;\n" +
				"        }\n" +
				"\n" +
				"        #searchByType {\n" +
				"            flex: 1;\n" +
				"        }\n" +
				"\n" +
				"\t\t#container {\n" +
				"\t\t\tmargin-top:150px;\n" +
				"            max-width: 1200px;\n" +
				"            width: 100%;\n" +
				"\t\t}\n" +
				"        #jsonDisplay {\n" +
				"            max-width: 1200px;\n" +
				"            width: 100%;\n" +
				"        }\n" +
				"\n" +
				"        .json-container {\n" +
				"            background-color: #ffffff;\n" +
				"            border-radius: 8px;\n" +
				"            box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);\n" +
				"            margin: 15px 0;\n" +
				"            padding: 15px;\n" +
				"            transition: all 0.2s ease-in-out;\n" +
				"\t\t\tcursor:pointer;\n" +
				"        }\n" +
				"\n" +
				"        .json-container:hover {\n" +
				"            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);\n" +
				"        }\n" +
				"\t\t\n" +
				"\t\t.json-header {\n" +
				"\t\t\tdisplay: flex;\n" +
				"\t\t\tjustify-content: space-between;\n" +
				"\t\t\talign-items: center;\n" +
				"\t\t}\n" +
				"\n" +
				"        .json-item {\n" +
				"            display: flex;\n" +
				"            padding: 10px 0;\n" +
				"            border-bottom: 1px solid #ecf0f1;\n" +
				"        }\n" +
				"\n" +
				"        .json-item:last-child {\n" +
				"            border-bottom: none;\n" +
				"        }\n" +
				"\n" +
				"        .json-label {\n" +
				"            font-weight: 500;\n" +
				"            color: #7f8c8d;\n" +
				"            flex-basis: 15%;\n" +
				"        }\n" +
				"\t\t\n" +
				"        .json-value {\n" +
				"            font-size: 1rem;\n" +
				"            color: #2c3e50;\n" +
				"\t\t\tflex-basis: 80%;\n" +
				"            justify-content: space-between;\n" +
				"            align-items: center;\n" +
				"\t\t\toverflow-wrap: break-word;\n" +
				"\t\t\toverflow-y: auto;\n" +
				"\t\t\tmin-height: 50px;\n" +
				"\t\t\tbox-sizing: border-box; \n" +
				"\t\t\tresize: vertical;\n" +
				"\t\t\tbackground-color: #faf9f7;\n" +
				"\t\t\tpadding: 10px;\n" +
				"\t\t\tborder-radius: 5px;\n" +
				"        }\n" +
				"\t\t.api-code-container {\n" +
				"            font-size: 1rem;\n" +
				"            color: #2c3e50;\n" +
				"\t\t\tflex-basis: 85%;\n" +
				"            justify-content: space-between;\n" +
				"            align-items: center;\n" +
				"\t\t\toverflow-wrap: break-word;\n" +
				"\t\t\toverflow-y: auto;\n" +
				"\t\t\tmin-height: 50px;\n" +
				"\t\t}\n" +
				"\t\t.json-copy-button {\n" +
				"            flex-basis: 5%;\n" +
				"\t\t\t\n" +
				"\t\t}\n" +
				"\n" +
				"        .copy-btn {\n" +
				"            cursor: pointer;\n" +
				"            background-color: #333333;\n" +
				"            color: white;\n" +
				"            border: none;\n" +
				"            border-radius: 50px;\n" +
				"            padding: 5px 15px;\n" +
				"            font-size: 0.9rem;\n" +
				"            display: flex;\n" +
				"            align-items: center;\n" +
				"            transition: background-color 0.2s ease;\n" +
				"            margin-left: 10px;\n" +
				"\t\t\tmin-width: 100px;\n" +
				"        }\n" +
				"\n" +
				"        .copy-btn:hover {\n" +
				"            background-color: #111111;\n" +
				"        }\n" +
				"\n" +
				"        .copy-btn svg {\n" +
				"            margin-right: 6px;\n" +
				"            fill: white;\n" +
				"        }\n" +
				"\t\t.details-btn {\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t\tbackground-color: #DF0522;\n" +
				"\t\t\tcolor: white;\n" +
				"\t\t\tborder: none;\n" +
				"\t\t\tborder-radius: 50px;\n" +
				"\t\t\twidth: 100px;\n" +
				"\t\t\tpadding: 5px 15px;\n" +
				"\t\t\ttext-align:center;\n" +
				"\t\t\ttransition: background-color 0.2s ease;\n" +
				"\t\t}\n" +
				"\t\t.details-btn:hover {\n" +
				"\t\t\tbackground-color: #c4041e;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t.details-btn svg {\n" +
				"\t\t\tfill: white;\n" +
				"\t\t}\n" +
				"        .toggle-btn {\n" +
				"            cursor: pointer;\n" +
				"            color: #3498db;\n" +
				"            display: inline-block;\n" +
				"            font-size: 1.2rem; \n" +
				"            font-weight: 500;\n" +
				"            margin-bottom: 10px;\n" +
				"            padding: 5px; \n" +
				"            border-radius: 5px;\n" +
				"            transition: background-color 0.2s;\n" +
				"        }\n" +
				"\n" +
				"        .toggle-btn:hover {\n" +
				"            background-color: #e1e1e1; \n" +
				"        }\n" +
				"\n" +
				"        .json-details {\n" +
				"            margin-top: 10px;\n" +
				"        }\n" +
				"\n" +
				"        .hidden {\n" +
				"            display: none;\n" +
				"        }\n" +
				"\n" +
				"\n" +
				"        #pagination {\n" +
				"            margin-top: 20px;\n" +
				"            display: flex;\n" +
				"            justify-content: center;\n" +
				"            gap: 10px;\n" +
				"        }\n" +
				"\n" +
				"        .pagination-btn {\n" +
				"            padding: 8px 12px;\n" +
				"            background-color: #DF0522;\n" +
				"            color: white;\n" +
				"            border: none;\n" +
				"            border-radius: 5px;\n" +
				"            cursor: pointer;\n" +
				"            transition: background-color 0.2s ease;\n" +
				"        }\n" +
				"\n" +
				"        .pagination-btn:hover {\n" +
				"            background-color: #c4041e;\n" +
				"        }\n" +
				"\n" +
				"        .pagination-btn.disabled {\n" +
				"            background-color: #bdc3c7;\n" +
				"            cursor: default;\n" +
				"        }\n" +
				"\n" +
				"        #page-info {\n" +
				"            margin-bottom: 10px;\n" +
				"            font-size: 1rem;\n" +
				"            color: #7f8c8d;\n" +
				"        }\n" +
				"        #notification {\n" +
				"            position: fixed;\n" +
				"            bottom: 20px;\n" +
				"            left: 50%;\n" +
				"            transform: translateX(-50%);\n" +
				"            background-color: #3498db;\n" +
				"            color: white;\n" +
				"            padding: 12px 24px;\n" +
				"            border-radius: 50px;\n" +
				"            font-size: 1rem;\n" +
				"            opacity: 0;\n" +
				"            pointer-events: none;\n" +
				"            transition: opacity 0.5s ease-in-out;\n" +
				"\t\t\tz-index: 9999;\n" +
				"        }\n" +
				"\n" +
				"        #notification.show {\n" +
				"            opacity: 1;\n" +
				"            pointer-events: auto;\n" +
				"        }\n" +
				"\t\t#resultSize {\n" +
				"\t\t\tfont-weight: 600;\n" +
				"\t\t\tpadding-bottom: 5px;\t\n" +
				"\t\t}\n" +
				"\t\t.apiCode {\n" +
				"\t\t\tpadding-top: 15px;\n" +
				"\t\t\tpadding-bottom: 5px;\n" +
				"\t\t}\n" +
				"\t\t.subtitleLabel {\n" +
				"\t\t\tfont-size:8pt;\n" +
				"\t\t\tcolor: #888888;\n" +
				"\t\t\tfont-weight: 800;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"        .overlay {\n" +
				"            position: fixed;\n" +
				"            top: 0;\n" +
				"            left: 0;\n" +
				"            width: 100%;\n" +
				"            height: 100%;\n" +
				"            background-color: rgba(0, 0, 0, 0.7);\n" +
				"            display: none;\n" +
				"            z-index: 1001; \n" +
				"        }\n" +
				"\t\t.overlay:hover {\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t}\n" +
				"\n" +
				"        .open-btn {\n" +
				"            padding: 12px 20px;\n" +
				"            background-color: #00e0ff;\n" +
				"            color: #121212;\n" +
				"            font-size: 1rem;\n" +
				"            border: none;\n" +
				"            border-radius: 30px;\n" +
				"            cursor: pointer;\n" +
				"            transition: transform 0.2s ease-in-out;\n" +
				"        }\n" +
				"\n" +
				"        .open-btn:hover {\n" +
				"            transform: scale(1.1);\n" +
				"            background-color: #00b8cc;\n" +
				"        }\n" +
				"\n" +
				"        .dialog {\n" +
				"            background: #FFFFFF;\n" +
				"            border-radius: 12px;\n" +
				"            width: 80%;\n" +
				"            padding: 25px 50px 50px 50px;\n" +
				"            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.6);\n" +
				"            transform: scale(0);\n" +
				"            animation: scaleIn 0.4s forwards;\n" +
				"\t\t\tdisplay: none;\n" +
				"            position: fixed;\n" +
				"            z-index: 1002; \n" +
				"\t\t\tfont-size: 10pt;\n" +
				"\t\t\toverflow-y: scroll;\n" +
				"\t\t\tmax-height: 80vh;\n" +
				"        }\n" +
				"\t\t.table-container {  \n" +
				"\t\t\tbox-shadow: 0 0 10px rgba(0, 0, 0, 0.2);\n" +
				"\t\t\tbox-sizing: content-box; \n" +
				"\t\t\tresize: vertical;\n" +
				"\t\t\tbackground-color: #DF0522;\n" +
				"\t\t\tmin-height: 100px; \n" +
				"\t\t\tmax-height: 300px;\n" +
				"\t\t\toverflow-y: scroll;\n" +
				"\t\t\tborder-radius: 5px; \n" +
				"            border-bottom: 8px solid #DF0522; \n" +
				"\t\t}\n" +
				"        table {\n" +
				"            width: 100%;\n" +
				"\t\t\tbackground-color:white;\n" +
				"            border-collapse: collapse;\n" +
				"        }\n" +
				"\n" +
				"        th, td {\n" +
				"            padding: 8px;\n" +
				"            text-align: left;\t\n" +
				"            border-bottom: 0.1px solid #CCC;\n" +
				"\n" +
				"\t\t\tpadding-left: 40px;\n" +
				"        }\n" +
				"        th {\n" +
				"            background-color: #DF0522;\n" +
				"            color: #fff;\n" +
				"            text-transform: uppercase; \n" +
				"            position: sticky;\n" +
				"            top: 0;\n" +
				"            z-index: 1;\n" +
				"        }\n" +
				"        td {\n" +
				"            background-color: #EFFFF;\n" +
				"        }\n" +
				"\n" +
				"        tr:hover td {\n" +
				"            transition: background-color 0.3s ease-in-out;\n" +
				"\t\t\tcursor:pointer;\n" +
				"\t\t\tbackground-color:#383838;\n" +
				"\t\t\tcolor:white;\n" +
				"        }\n" +
				"\n" +
				"        th:hover {\n" +
				"            text-decoration: underline;\n" +
				"        }\n" +
				"\n" +
				"        @keyframes scaleIn {\n" +
				"            0% {\n" +
				"                transform: scale(0);\n" +
				"                opacity: 0;\n" +
				"            }\n" +
				"            100% {\n" +
				"                transform: scale(1);\n" +
				"                opacity: 1;\n" +
				"            }\n" +
				"        }\n" +
				"\t\t.ve-detail-title {\n" +
				"\t\t\tfont-weight: bold;\n" +
				"\t\t\ttext-align: right;\n" +
				"\t\t\tflex: 30%;\n" +
				"\t\t}\n" +
				"\t\t.ve-detail-value {\n" +
				"\t\t\tflex:50%;\n" +
				"\t\t}\n" +
				"\t\t.ve-detail-header {\n" +
				"\t\t\tdisplay: flex;\n" +
				"\t\t\tflex-direction: row;\n" +
				"\t\t\tflex-wrap:wrap;\n" +
				"\t\t\tmargin-top: 5px;\n" +
				"\t\t\tflex: 50%;\n" +
				"\t\t}\n" +
				"\t\t.ve-header {\n" +
				"\t\t\tmargin-bottom: 20px;\n" +
				"\t\t\tmargin-top:20px;\n" +
				"\t\t\tdisplay: flex;\n" +
				"\t\t\tflex-direction: row;\n" +
				"\t\t\tflex-wrap: wrap;\n" +
				"\t\t}\n" +
				"\t\t.close-btn {\n" +
				"\t\t\tposition:absolute;\n" +
				"\t\t\ttop: 10px;\n" +
				"\t\t\tright: 10px;\n" +
				"\t\t\tfont-size: 20pt;\n" +
				"\t\t\tfont-weight: bold;\n" +
				"\t\t\tbackground-color: transparent;\n" +
				"\t\t\tborder-width: 0px;\n" +
				"\t\t}\n" +
				"\t\t.close-btn:hover {\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t}\n" +
				"\t\t.absolute-ve {\n" +
				"\t\t\tbackground-color: #DF0522;\n" +
				"\t\t\tcolor: white;\n" +
				"\t\t\ttext-align:center;\n" +
				"\t\t\tborder-radius: 20px;\n" +
				"\t\t\tpadding:2px 10px;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 * {\n" +
				"\t\t\tbox-sizing: border-box;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx {\n" +
				"\t\t\t-webkit-user-select: none;\n" +
				"\t\t\tuser-select: none;\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t\tpadding: 6px 8px;\n" +
				"\t\t\tborder-radius: 6px;\n" +
				"\t\t\toverflow: hidden;\n" +
				"\t\t\ttransition: all 0.2s ease;\n" +
				"\t\t\tdisplay: inline-block;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx:not(:last-child) {\n" +
				"\t\t\tmargin-right: 6px;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx:hover {\n" +
				"\t\t\tbackground: rgba(0,119,255,0.06);\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx span {\n" +
				"\t\t\tfloat: left;\n" +
				"\t\t\tvertical-align: middle;\n" +
				"\t\t\ttransform: translate3d(0, 0, 0);\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx span:first-child {\n" +
				"\t\t\tposition: relative;\n" +
				"\t\t\twidth: 18px;\n" +
				"\t\t\theight: 18px;\n" +
				"\t\t\tborder-radius: 4px;\n" +
				"\t\t\ttransform: scale(1);\n" +
				"\t\t\tborder: 1px solid #cccfdb;\n" +
				"\t\t\ttransition: all 0.2s ease;\n" +
				"\t\t\tbox-shadow: 0 1px 1px rgba(0,16,75,0.05);\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx span:first-child svg {\n" +
				"\t\t\tposition: absolute;\n" +
				"\t\t\ttop: 3px;\n" +
				"\t\t\tleft: 2px;\n" +
				"\t\t\tfill: none;\n" +
				"\t\t\tstroke: #fff;\n" +
				"\t\t\tstroke-width: 2;\n" +
				"\t\t\tstroke-linecap: round;\n" +
				"\t\t\tstroke-linejoin: round;\n" +
				"\t\t\tstroke-dasharray: 16px;\n" +
				"\t\t\tstroke-dashoffset: 16px;\n" +
				"\t\t\ttransition: all 0.3s ease;\n" +
				"\t\t\ttransition-delay: 0.1s;\n" +
				"\t\t\ttransform: translate3d(0, 0, 0);\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx span:last-child {\n" +
				"\t\t\tpadding-left: 8px;\n" +
				"\t\t\tline-height: 18px;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .cbx:hover span:first-child {\n" +
				"\t\t\tborder-color: #07f;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .inp-cbx {\n" +
				"\t\t\tposition: absolute;\n" +
				"\t\t\tvisibility: hidden;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .inp-cbx:checked + .cbx span:first-child {\n" +
				"\t\t\tbackground: #07f;\n" +
				"\t\t\tborder-color: #07f;\n" +
				"\t\t\tanimation: wave-4 0.4s ease;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .inp-cbx:checked + .cbx span:first-child svg {\n" +
				"\t\t\tstroke-dashoffset: 0;\n" +
				"\t\t}\n" +
				"\t\t.checkbox-wrapper-4 .inline-svg {\n" +
				"\t\t\tposition: absolute;\n" +
				"\t\t\twidth: 0;\n" +
				"\t\t\theight: 0;\n" +
				"\t\t\tpointer-events: none;\n" +
				"\t\t\tuser-select: none;\n" +
				"\t\t}\n" +
				"\t\t@media screen and (max-width: 640px) {\n" +
				"\t\t\t.checkbox-wrapper-4 .cbx {\n" +
				"\t\t\t\twidth: 100%;\n" +
				"\t\t\t\tdisplay: inline-block;\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\t@-moz-keyframes wave-4 {\n" +
				"\t\t\t50% {\n" +
				"\t\t\t\ttransform: scale(0.9);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\t@-webkit-keyframes wave-4 {\n" +
				"\t\t\t50% {\n" +
				"\t\t\t\ttransform: scale(0.9);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\t@-o-keyframes wave-4 {\n" +
				"\t\t\t50% {\n" +
				"\t\t\t\ttransform: scale(0.9);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\t@keyframes wave-4 {\n" +
				"\t\t\t50% {\n" +
				"\t\t\t\ttransform: scale(0.9);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\t.tooltip {\n" +
				"\t\t\tposition: relative;\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t.tooltip .tooltiptext {\n" +
				"\t\t  visibility: hidden;\n" +
				"\t\t  width: 120px;\n" +
				"\t\t  background-color: #555;\n" +
				"\t\t  color: #fff;\n" +
				"\t\t  text-align: center;\n" +
				"\t\t  border-radius: 5px;\n" +
				"\t\t  padding: 5px;\n" +
				"\t\t  position: absolute;\n" +
				"\t\t  z-index: 1;\n" +
				"\t\t  bottom: 100%;\n" +
				"\t\t  left: 50%;\n" +
				"\t\t  margin-left: -60px;\n" +
				"\t\t  opacity: 0;\n" +
				"\t\t  transition: opacity 0.3s;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\t.tooltip:hover .tooltiptext {\n" +
				"\t\t  visibility: visible;\n" +
				"\t\t  opacity: 1;\n" +
				"\t\t}\n" +
				"\t\t.tooltip:hover {\n" +
				"\t\t\tbackground-color: #ff5d00;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"        .tabs-container {\n" +
				"            display: flex;\n" +
				"            flex-direction: column;\n" +
				"            width: 300px;\n" +
				"            background-color: white;\n" +
				"            border-radius: 12px;\n" +
				"            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
				"        }\n" +
				"\n" +
				"        .tab-header {\n" +
				"            display: flex;\n" +
				"\t\t\tmax-width: 500px;\n" +
				"            background-color: #FFFFFF;\n" +
				"            border-radius: 30px;\n" +
				"\t\t\tmargin: auto;\n" +
				"            overflow: hidden;\n" +
				"            box-shadow: 0 0 7px 1px rgba(0, 0, 0, 0.1);\n" +
				"\t\t\t\n" +
				"        }\n" +
				"\n" +
				"        .tab-header div {\n" +
				"            flex: 1;\n" +
				"            padding: 7px 12px;\n" +
				"            text-align: center;\n" +
				"            cursor: pointer;\n" +
				"            background-color: #FFFFFF;\n" +
				"            font-weight: bold;\n" +
				"\t\t\tfont-size: 10pt;\n" +
				"\t\t\tborder-radius: 20px;\n" +
				"\t\t\tmargin: 7px;\n" +
				"        }\n" +
				"\n" +
				"        .tab-header div:hover {\n" +
				"            background-color: #DF0522;\n" +
				"\t\t\tcolor: white;\n" +
				"        }\n" +
				"\n" +
				"        .tab-header .active {\n" +
				"            background-color: #DF0522;\n" +
				"\t\t\tcolor:white;\n" +
				"        }\n" +
				"\n" +
				"        .tab-content {\n" +
				"            display: none;\n" +
				"\t\t\tpadding-top: 20px;\n" +
				"        }\n" +
				"\n" +
				"        .tab-content.active {\n" +
				"            display: block;\n" +
				"        }\n" +
				"        .custom-combobox {\n" +
				"            width: 200px; \n" +
				"\t\t\tmargin-top: -2px;\n" +
				"            border: none ;\n" +
				"            border-radius: 20px; \n" +
				"            background-color: #DF0522;\n" +
				"            transition: border-color 1s ease;\n" +
				"            position: relative; \n" +
				"        }\n" +
				"\t\t.custom-combobox select {\n" +
				"\t\t\tappearance: none;\n" +
				"\t\t\twidth: 200px;\n" +
				"\t\t\tborder: none ;\n" +
				"\t\t\tborder-radius: 20px; \n" +
				"\t\t\tbackground-color: #DF0522;\n" +
				"            color: white;\n" +
				"            padding: 5px 20px;\n" +
				"\t\t\tcursor: pointer;\n" +
				"\t\t}\n" +
				"\n" +
				"        .custom-combobox select:hover {\n" +
				"            border-color: #DF0522;\n" +
				"        }\n" +
				"\n" +
				"        .custom-combobox select:focus {\n" +
				"            outline: none;\n" +
				"            border-color: #1abc9c;\n" +
				"        }\n" +
				"\n" +
				"        .custom-combobox::-ms-expand {\n" +
				"            display: none;\n" +
				"        }\n" +
				"\n" +
				"        .custom-combobox::after {\n" +
				"            content: \" ▼\";\n" +
				"            color: white;\n" +
				"            font-size: 14px;\n" +
				"            position: absolute;\n" +
				"            top: 50%;\n" +
				"            right: 10px;\n" +
				"            transform: translateY(-50%);\n" +
				"            pointer-events: none;\n" +
				"        }\n" +
				"\n" +
				"\t\t.table-container-title {\n" +
				"\t\t\tpadding: 10px 0px;\n" +
				"\t\t\ttext-align: center;\n" +
				"\t\t\tbackground-color: #DF0522;\n" +
				"\t\t\tcolor: white;\n" +
				"\t\t\twidth: auto;\n" +
				"\t\t\tfont-weight: bold;\n" +
				"\t\t}\n" +
				"\t\t.table-container-title-chip {\n" +
				"\t\t\tbackground-color:white; \n" +
				"\t\t\tpadding: 3px 15px; \n" +
				"\t\t\tborder-radius: 50px; \n" +
				"\t\t\tcolor: #DF0522;\n" +
				"\t\t}\n" +
				"\t\tpre {\n" +
				"            font-family: inherit;\n" +
				"            white-space: pre-wrap; \n" +
				"\t\t\tmargin: 0px;\n" +
				"        }\n" +
				"\t\t\n" +
				"    </style>\n" +
				"</head>\n" +
				"<body>\n" +
				"\n" +
				"<div id=\"header\">\n" +
				"\t<h1>Dao2Jdbc Code XForm Report</h1>\n" +
				"\n" +
				"\t<div id=\"searchContainer\">\n" +
				"\t\t<input type=\"text\" id=\"searchByDescription\" placeholder=\"デスクリプションで検索\" onkeyup=\"searchJSON()\">\n" +
				"\n" +
				"\t\t<select id=\"searchByType\" onchange=\"searchJSON()\">\n" +
				"\t\t\t<option value=\"\">SQLタイプ</option>\n" +
				"\t\t\t<option value=\"SELECT\">Select</option>\n" +
				"\t\t\t<option value=\"UPDATE\">Update</option>\n" +
				"\t\t\t<option value=\"INSERT\">Insert</option>\n" +
				"\t\t\t<option value=\"DELETE\">Delete</option>\n" +
				"\t\t</select>\n" +
				"\t\t<select id=\"paginationNumber\" onchange=\"changePaginationRange()\">\n" +
				"\t\t\t<option value=\"5\">1ページのアイテム数</option>\n" +
				"\t\t\t<option value=\"5\">5 アイテム</option>\n" +
				"\t\t\t<option value=\"10\">10 アイテム</option>\n" +
				"\t\t\t<option value=\"50\">50 アイテム</option>\n" +
				"\t\t\t<option value=\"100\">100 アイテム</option>\n" +
				"\t\t\t<option value=\"200\">200 アイテム</option>\n" +
				"\t\t\t<option value=\"500\">500 アイテム</option>\n" +
				"\t\t\t<option value=\"1000\">1000 アイテム</option>\n" +
				"\t\t</select>\n" +
				"\t</div>\n" +
				"\t<div id=\"page-info\"></div>\n" +
				"\t</div>\n" +
				"\t\n" +
				"</div>\n" +
				"\n" +
				"<div id=\"container\">\n" +
				"\t<div id=\"pagination\"></div>\n" +
				"\t<div id=\"jsonDisplay\"></div>\n" +
				"</div>\n" +
				"\n" +
				"\n" +
				"<div class=\"overlay\" id=\"overlay\" onclick=\"closeDialog()\"></div>\n" +
				"\n" +
				"<div class=\"dialog\">\n" +
				"    <h1>仮想表定義情報</h1>\n" +
				"\t\n" +
				"\t<div class=\"tab-header\">\n" +
				"\t\t<div class=\"tab\" data-target=\"tab1\">全般</div>\n" +
				"\t\t<div class=\"tab\" data-target=\"tab2\">メインエンティティ</div>\n" +
				"\t</div>\n" +
				"\t<div class=\"tab-content\" id=\"tab1\">\n" +
				"\t\t<div class=\"ve-header\">\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t仮想表名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-displayname\">\n" +
				"\t\t\t\t\t${displayName}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\tカテゴリ名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-categoryName\">\n" +
				"\t\t\t\t\t${categoryName}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t仮想表ID：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-veid\">\n" +
				"\t\t\t\t\t${veId}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\tカテゴリID：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-categoryid\">\n" +
				"\t\t\t\t\t${categoryId}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t仮想表タイプ：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-vetype\">\n" +
				"\t\t\t\t\t${veType}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<span style=\"display:none\" id=\"ve-info-absolutevecode\"> </span>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" id=\"ve-info-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> 仮想項目 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t<div>\n" +
				"\t\t\t<button class=\"copy-btn\" id=\"copy-absolute-ve-btn\" style=\"margin-top: 30px;margin-left:-2px;font-weight:bold;\" onclick=\"copyToClipboard('ve-info-absolutevecode')\">完全仮想項目をコピー</button>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"ve-header\">\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t外部キー名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\">\n" +
				"\t\t\t\t\t<div  class=\"custom-combobox\"> \n" +
				"\t\t\t\t\t\t<select id=\"ve-foreign-keys\">\n" +
				"\t\t\t\t\t\t\t<option> - </option>\n" +
				"\t\t\t\t\t\t</select>\n" +
				"\t\t\t\t\t</div>\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t参照エンティティ名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-foreign-keys-ref\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t外部キータイプ：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-foreign-keys-join-type\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" id=\"ve-info-foreign-keys-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> 外部キー </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-foreign-keys-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" style=\"margin-top: 50px;\" id=\"ve-info-filter-conditions-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> フィルター条件 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-filter-conditions\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" style=\"margin-top: 50px;\" id=\"ve-info-search-conditions-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> 検索条件 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-search-conditions\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" style=\"margin-top: 50px;\" id=\"ve-info-sort-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> ソート項目 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-sort-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" style=\"margin-top: 50px;\" id=\"ve-info-group-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> グループ項目 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-group-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t</div>\n" +
				"\t<div class=\"tab-content\" id=\"tab2\"> \n" +
				"\t\t<div class=\"ve-header\">\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\tメインエンティティ名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-name\">\n" +
				"\t\t\t\t\t${entityName}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\tデータベース名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-database\">\n" +
				"\t\t\t\t\t${databaseName}\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t<div class=\"table-container\" id=\"ve-info-entity-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> メインエンティティ項目 </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-entity-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t<div class=\"ve-header\">\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\tユニークキー名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-name\">\n" +
				"\t\t\t\t\t<div  class=\"custom-combobox\"> \n" +
				"\t\t\t\t\t\t<select id=\"ve-entity-unique-keys\">\n" +
				"\t\t\t\t\t\t\t<option> - </option>\n" +
				"\t\t\t\t\t\t</select>\n" +
				"\t\t\t\t\t</div>\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" id=\"ve-info-entity-unique-keys-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> ユニークキー </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-entity-unique-keys-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t\t<div class=\"ve-header\">\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t外部キー名：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-name\">\n" +
				"\t\t\t\t\t<div  class=\"custom-combobox\"> \n" +
				"\t\t\t\t\t\t<select id=\"ve-entity-foreign-keys\">\n" +
				"\t\t\t\t\t\t\t<option> - </option>\n" +
				"\t\t\t\t\t\t</select>\n" +
				"\t\t\t\t\t</div>\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t参照エンティティ：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-foreign-keys-ref\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t外部キータイプ：\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\" id=\"ve-info-entity-foreign-keys-join-type\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div class=\"ve-detail-header\">\n" +
				"\t\t\t\t<div class=\"ve-detail-title\">\n" +
				"\t\t\t\t\t\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t\t<div class=\"ve-detail-value\">\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t</div>\t\n" +
				"\t\t</div>\n" +
				"\t\t\n" +
				"\t\t<div class=\"table-container\" id=\"ve-info-entity-foreign-keys-columns-container\">\n" +
				"\t\t\t<div class=\"table-container-title\">\n" +
				"\t\t\t\t<span class=\"table-container-title-chip\"> 外部キー </span>\n" +
				"\t\t\t</div>\n" +
				"\t\t\t<div id=\"ve-info-entity-foreign-keys-columns\">\n" +
				"\t\t\t</div>\n" +
				"\t\t</div>\n" +
				"\t</div>\n" +
				"    <div>\n" +
				"        <button class=\"close-btn\" style=\"font-size:15pt\" onclick=\"closeDialog()\">X</button>\n" +
				"    </div>\n" +
				"</div>\n" +
				"<div id=\"notification\">Copied to clipboard!</div>\n" +
				"\n" +
				"<script>\n" +
				"    const jsonArray = [{\"description\":\"test\",\"sqlScript\":\"select\\r\\n    *\\r\\nfrom\\r\\n    test;\",\"apiCodes\":{\"FIND_LIST\":\"List&lt;GRecord&gt; result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.findList();\",\"FIND_RECORD\":\"GRecord result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.findRecord();\",\"FIND_RECORD_SET\":\"GRecordSet result = select(\\\"test\\\", \\\"test\\\")\\r\\n.fields(colsAll())\\r\\n.findRecordSet();\"},\"type\":\"SELECT\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}},{\"description\":\"test2\",\"sqlScript\":\"select\\r\\n    *\\r\\nfrom\\r\\n    test2\\r\\nwhere\\r\\n    col1 = ?\",\"apiCodes\":{\"FIND_LIST-WHERE\":\"Object[] whereParams = new Object[]{};\\r\\nList&lt;GRecord&gt; result = select(\\\"test2\\\", \\\"test2\\\")\\r\\n.fields(colsAll())\\r\\n.where(exp($(\\\"col1 = ?\\\"), whereParams))\\r\\n.findList();\",\"FIND_RECORD-WHERE\":\"Object[] whereParams = new Object[]{};\\r\\nGRecord result = select(\\\"test2\\\", \\\"test2\\\")\\r\\n.fields(colsAll())\\r\\n.where(exp($(\\\"col1 = ?\\\"), whereParams))\\r\\n.findRecord();\",\"FIND_RECORD_SET-WHERE\":\"Object[] whereParams = new Object[]{};\\r\\nGRecordSet result = select(\\\"test2\\\", \\\"test2\\\")\\r\\n.fields(colsAll())\\r\\n.where(exp($(\\\"col1 = ?\\\"), whereParams))\\r\\n.findRecordSet();\"},\"type\":\"SELECT\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}},{\"description\":\"test2\",\"sqlScript\":\"delete *\\r\\nfrom\\r\\n    test2\\r\\nwhere\\r\\n    col1 = ?\",\"apiCodes\":{},\"type\":\"DELETE\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}},{\"description\":\"test2\",\"sqlScript\":\"update\\r\\nfrom\\r\\n    test2\\r\\nset\\r\\n    col1 = ?\\r\\nwhere\\r\\n    col2 = ?\",\"apiCodes\":{},\"type\":\"UPDATE\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}},{\"description\":\"test2\",\"sqlScript\":\"insert into\\r\\n    test2\\r\\nvalues\\r\\n(?, ?)\",\"apiCodes\":{\"EXECUTE\":\"GRecord record = createRecord();\\r\\n\\r\\nint count = insert(\\\"test2\\\")\\r\\n.values(record)\\r\\n.execute();\",\"EXECUTE_BATCH\":\"List&lt;GRecord&gt; recordList = new ArrayList<>();\\r\\nGRecord record = createRecord();\\r\\nrecordList.add(record);\\r\\n\\r\\nint count = insert(\\\"test2\\\")\\r\\n.values(recordList)\\r\\n.executeBatch();\",\"EXECUTE_LIST\":\"List&lt;GRecord&gt; recordList = new ArrayList<>();\\r\\nGRecord record = createRecord();\\r\\nrecordList.add(record);\\r\\n\\r\\nint count = insert(\\\"test2\\\")\\r\\n.values(recordList)\\r\\n.executeList();\"},\"type\":\"INSERT\",\"virtualEntity\":{\"displayName\":\"ve1\",\"veId\":\"aaaa-aaaa-aaaa-aaaa-aaaa\",\"categoryId\":\"bbbb-bbbb-bbbb-bbbb-bbbb\",\"categoryName\":\"categoryTest\",\"veType\":\"1\",\"refEntity\":{\"name\":\"testEntity\",\"phyName\":\"testPhy\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}],\"uniqueKeys\":[{\"name\":\"uk1\",\"uniqueKeyColumns\":[{\"name\":\"testCol\",\"phyName\":\"testCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true},{\"name\":\"testCol2\",\"phyName\":\"testCol2\",\"size\":255,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":false}]}],\"foreignKeys\":[{\"name\":\"fk1\",\"referenceEntity\":{\"name\":\"refEntity\",\"phyName\":\"phyRefEntity\",\"databaseName\":\"testDb\",\"primaryKey\":null,\"columns\":[{\"name\":\"refCol\",\"phyName\":\"phyRefCol\",\"size\":10,\"dataType\":\"STRING\",\"decimalSize\":null,\"notNull\":true,\"primaryKey\":true}],\"uniqueKeys\":[],\"foreignKeys\":[]},\"foreignKeyColumns\":[{\"referenceColumn\":\"refCol\",\"sourceColumn\":\"testCol\",\"constValue\":\"test\"}],\"joinType\":\"INNER_JOIN\"}]},\"columns\":[{\"name\":\"testVColumn\",\"displayName\":\"testVColumn\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol\"},{\"name\":\"testVColumn2\",\"displayName\":\"testVColumn2\",\"fixedValue\":null,\"aggregateFunction\":null,\"format\":null,\"refColumn\":\"testEntity.testCol2\"}],\"absoluteVirtualColumnCode\":\"-\",\"foreignKeys\":[],\"filterConditions\":[],\"searchConditions\":[],\"groupConditions\":[],\"sortConditions\":[]}}]\n" +
				"\tvar itemsPerPage = 10;\n" +
				"    let currentPage = 1;\n" +
				"    function displayJSON(jsonArray) {\n" +
				"        const displayElement = document.getElementById('jsonDisplay');\n" +
				"        displayElement.innerHTML = ''; \n" +
				"\n" +
				"        const start = (currentPage - 1) * itemsPerPage;\n" +
				"        const end = start + itemsPerPage;\n" +
				"        const paginatedArray = jsonArray.slice(start, end);\n" +
				"        paginatedArray.forEach((obj, index) => {\n" +
				"            const container = document.createElement('div');\n" +
				"            container.className = 'json-container';\n" +
				"            container.id = `json-${index}`;\n" +
				"\t\t\tcontainer.onclick = () => toggleDetails(index);\n" +
				"            let html = `\n" +
				"                <div class=\"json-header\">\n" +
				"                    <div class=\"Json-header-title\">\n" +
				"                        <span>▶</span>\n" +
				"                        ${obj.description} / ${obj.type}\n" +
				"                    </div>\n" +
				"                    <span>{detail_btn}</span>\n" +
				"                </div>\n" +
				"                <div id=\"details-${index}\" class=\"json-details hidden\" onclick=\"event.stopPropagation();\">\n" +
				"                <div class=\"apiCode\"> <b>SQL スクリプト</b> <br> <span class='subtitleLabel'>変換前のSQLスクリプト</span></div>\n" +
				"\t\t\t\t\t<div class=\"json-item\">\n" +
				"                        <span class=\"json-label\">SQL スクリプト:</span> \n" +
				"                        <span class=\"json-value\">\n" +
				"                            <pre><span id=\"sqlScript-${index}\">${obj.sqlScript}</span></pre>\n" +
				"                        </span>\n" +
				"\t\t\t\t\t\t<span class=\"json-copy-button\">\n" +
				"\t\t\t\t\t\t\t<button class=\"copy-btn\" onclick=\"copyToClipboard('sqlScript-${index}')\">\n" +
				"                                <svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-clipboard\" viewBox=\"0 0 16 16\">\n" +
				"                                    <path d=\"M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z\"/>\n" +
				"                                    <path d=\"M5.5 1.5h-1v1h1v-1z\"/>\n" +
				"                                </svg>\n" +
				"                                コピー\n" +
				"                            </button>\n" +
				"\t\t\t\t\t\t</span>\n" +
				"                    </div>\n" +
				"\t\t\t\t\t<div class=\"apiCode\"> <b>API コード</b> <br> <span class='subtitleLabel'>変換後のGEF-JDBC APIコード</span></div>\n" +
				"\t\t\t\t\t{item1}\n" +
				"\t\t\t\t\t{item2}\n" +
				"\t\t\t\t\t{item3}\n" +
				"                </div>\n" +
				"            `;\n" +
				"\t\t\thtml = changeDetailButtonIfAbsoluteColumnExist(html, index, paginatedArray);\n" +
				"\t\t\tif (obj.type == \"SELECT\") {\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 1, 'FIND_LIST', paginatedArray, obj.type);\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 2, 'FIND_RECORD', paginatedArray, obj.type);\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 3, 'FIND_RECORD_SET', paginatedArray, obj.type);\n" +
				"\t\t\t} else {\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 1, 'EXECUTE', paginatedArray, obj.type);\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 2, 'EXECUTE_LIST', paginatedArray, obj.type);\n" +
				"\t\t\t\thtml = replaceApiCode(html, index, 3, 'EXECUTE_BATCH', paginatedArray, obj.type);\n" +
				"\t\t\t}\n" +
				"\t\t\tcontainer.innerHTML = html;\n" +
				"            displayElement.appendChild(container);\n" +
				"\t\t\tdocument.getElementById(\"btn-details-\"+index).addEventListener('click', function(event) {\n" +
				"\t\t\t\topenDialog(event, index, paginatedArray);\n" +
				"\t\t\t});\n" +
				"        });\n" +
				"\n" +
				"        displayPagination(jsonArray);\n" +
				"        displayPageInfo(jsonArray);\n" +
				"    }\n" +
				"\tfunction changeDetailButtonIfAbsoluteColumnExist(html, index, paginatedArray) {\n" +
				"\t\tconst absoluteCode = paginatedArray[index].virtualEntity.absoluteVirtualColumnCode;\n" +
				"\t\tconst btn = document.createElement('button');\n" +
				"\t\tbtn.id = \"btn-details-\"+index;\n" +
				"\t\tif (absoluteCode != \"-\") {\n" +
				"\t\t\tbtn.className = 'details-btn tooltip';\n" +
				"\t\t\tbtn.innerHTML = `<span class=\"tooltiptext\">完全仮想項目含む</span>\n" +
				"\t\t\t\t\t<div style=\"display:flex; justify-content:center; align-items:center; text-align:center; \">\n" +
				"\t\t\t\t\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-exclamation-circle\" viewBox=\"0 0 16 16\" style=\"margin-right:2px;\">\n" +
				"\t\t\t\t\t\t\t<path d=\"M8 1a7 7 0 1 0 7 7 7 7 0 0 0-7-7zM7.002 4a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7 6h2v5H7z\"/>\n" +
				"\t\t\t\t\t\t</svg>\n" +
				"\t\t\t\t\t詳細</div>`;\n" +
				"\t\t} else {\n" +
				"\t\tbtn.className = 'details-btn';\n" +
				"\t\t\tbtn.innerHTML = `詳細`;\n" +
				"\t\t}\n" +
				"\t\thtml = html.replace(\"{detail_btn}\", btn.outerHTML);\n" +
				"\t\treturn html;\n" +
				"\t}\n" +
				"\tfunction hideWhere(id) {\n" +
				"\t\tconst display = document.getElementById(id).style.display;\n" +
				"\t\tif (display == \"none\") {\n" +
				"\t\t\tdocument.getElementById(id).style.display=\"flex\";\n" +
				"\t\t} else {\n" +
				"\t\t\tdocument.getElementById(id).style.display=\"none\";\n" +
				"\t\t}\n" +
				"\t}\n" +
				"\tfunction replaceApiCode(html, objIndex, itemIndex, apiType, paginatedArray, crudType) {\n" +
				"\t\tlet codeWhere = paginatedArray[objIndex].apiCodes[apiType+\"-WHERE\"];\n" +
				"\t\tlet codeWherePK = paginatedArray[objIndex].apiCodes[apiType+\"-WHERE_PK\"];\n" +
				"\t\tlet codeWhereUK = paginatedArray[objIndex].apiCodes[apiType+\"-WHERE_UK\"];\n" +
				"\t\tlet code = paginatedArray[objIndex].apiCodes[apiType];\n" +
				"\t\t\n" +
				"\t\tif (code != undefined) {\n" +
				"\t\t\treturn html.replace('{item'+itemIndex+'}', `\n" +
				"\t\t\t\t<div class=\"json-item\">\n" +
				"\t\t\t\t\t<span class=\"json-label\">${replaceToCamelCase(apiType)}:</span> \n" +
				"\t\t\t\t\t<span class=\"json-value\">\n" +
				"\t\t\t\t\t\t<pre><span id=\"apiCode-${objIndex}-${itemIndex}\">${code}</span></pre>\n" +
				"\t\t\t\t\t</span>\n" +
				"\t\t\t\t\t<span class=\"json-copy-button\">\n" +
				"\t\t\t\t\t\t<button class=\"copy-btn\" onclick=\"copyToClipboard('apiCode-${objIndex}-${itemIndex}')\">\n" +
				"\t\t\t\t\t\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-clipboard\" viewBox=\"0 0 16 16\">\n" +
				"\t\t\t\t\t\t\t\t<path d=\"M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z\"/>\n" +
				"\t\t\t\t\t\t\t\t<path d=\"M5.5 1.5h-1v1h1v-1z\"/>\n" +
				"\t\t\t\t\t\t\t</svg>\n" +
				"\t\t\t\t\t\t\t\tコピー\n" +
				"\t\t\t\t\t\t</button>\n" +
				"\t\t\t\t\t</span>\n" +
				"\t\t\t\t</div>\n" +
				"\t\t\t`);\n" +
				"\t\t} else if (codeWhere != undefined || codeWherePK != undefined || codeWhereUK != undefined) {\n" +
				"\t\t\tlet apiCodeHtml = `<div class=\"json-item\" style=\"display: flex; flex-direction: row;\">\n" +
				"\t\t\t\t\t<span class=\"json-label\" >${replaceToCamelCase(apiType)}:  `;\n" +
				"\t\t\tif (codeWhere != undefined) {\n" +
				"\t\t\t\tapiCodeHtml += `<div class=\"checkbox-container\">\n" +
				"\t\t\t\t\t\t\t<div class=\"checkbox-wrapper-4\">\n" +
				"\t\t\t\t\t\t\t  <input class=\"inp-cbx\" id=\"where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\" type=\"checkbox\" checked  onchange=\"hideWhere('where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}')\"/>\n" +
				"\t\t\t\t\t\t\t  <label class=\"cbx\" for=\"where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\"><span>\n" +
				"\t\t\t\t\t\t\t  <svg width=\"12px\" height=\"10px\">\n" +
				"\t\t\t\t\t\t\t\t<use xlink:href=\"#check-4\"></use>\n" +
				"\t\t\t\t\t\t\t  </svg></span><span>where</span></label>\n" +
				"\t\t\t\t\t\t\t  <svg class=\"inline-svg\">\n" +
				"\t\t\t\t\t\t\t\t<symbol id=\"check-4\" viewbox=\"0 0 12 10\">\n" +
				"\t\t\t\t\t\t\t\t  <polyline points=\"1.5 6 4.5 9 10.5 1\"></polyline>\n" +
				"\t\t\t\t\t\t\t\t</symbol>\n" +
				"\t\t\t\t\t\t\t  </svg>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t  </div>`;\n" +
				"\t\t   }\n" +
				"\t\t\tif (codeWherePK != undefined) {\n" +
				"\t\t\t\tapiCodeHtml += `<div class=\"checkbox-container\">\n" +
				"\t\t\t\t\t\t\t<div class=\"checkbox-wrapper-4\">\n" +
				"\t\t\t\t\t\t\t  <input class=\"inp-cbx\" id=\"wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\" type=\"checkbox\" checked onchange=\"hideWhere('wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}')\"/>\n" +
				"\t\t\t\t\t\t\t  <label class=\"cbx\" for=\"wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\"><span>\n" +
				"\t\t\t\t\t\t\t  <svg width=\"12px\" height=\"10px\">\n" +
				"\t\t\t\t\t\t\t\t<use xlink:href=\"#check-5\"></use>\n" +
				"\t\t\t\t\t\t\t  </svg></span><span>wherePK</span></label>\n" +
				"\t\t\t\t\t\t\t  <svg class=\"inline-svg\">\n" +
				"\t\t\t\t\t\t\t\t<symbol id=\"check-5\" viewbox=\"0 0 12 10\">\n" +
				"\t\t\t\t\t\t\t\t  <polyline points=\"1.5 6 4.5 9 10.5 1\"></polyline>\n" +
				"\t\t\t\t\t\t\t\t</symbol>\n" +
				"\t\t\t\t\t\t\t  </svg>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t  </div>`;\n" +
				"\t\t\t}\n" +
				"\t\t\tif (codeWhereUK != undefined) {\t\n" +
				"\t\t\t\tapiCodeHtml += `<div class=\"checkbox-container\">\n" +
				"\t\t\t\t\t\t\t<div class=\"checkbox-wrapper-4\">\n" +
				"\t\t\t\t\t\t\t  <input class=\"inp-cbx\" id=\"whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\" type=\"checkbox\" checked  onchange=\"hideWhere('whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}')\"/>\n" +
				"\t\t\t\t\t\t\t  <label class=\"cbx\" for=\"whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}-checkbox\"><span>\n" +
				"\t\t\t\t\t\t\t  <svg width=\"12px\" height=\"10px\">\n" +
				"\t\t\t\t\t\t\t\t<use xlink:href=\"#check-6\"></use>\n" +
				"\t\t\t\t\t\t\t  </svg></span><span>whereUK</span></label>\n" +
				"\t\t\t\t\t\t\t  <svg class=\"inline-svg\">\n" +
				"\t\t\t\t\t\t\t\t<symbol id=\"check-6\" viewbox=\"0 0 12 10\">\n" +
				"\t\t\t\t\t\t\t\t  <polyline points=\"1.5 6 4.5 9 10.5 1\"></polyline>\n" +
				"\t\t\t\t\t\t\t\t</symbol>\n" +
				"\t\t\t\t\t\t\t  </svg>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t  </div>`;\n" +
				"\t\t\t}\n" +
				"\t\t\tapiCodeHtml += `</span>\n" +
				"\t\t\t\t\t<div class=\"api-code-container\" >`;\n" +
				"\t\t\tif (codeWhere != undefined) {\n" +
				"\t\t\t\tapiCodeHtml += `<div style=\"display: flex; flex-direction: row; padding: 10px 0px;\" id=\"where-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}\">\n" +
				"\t\t\t\t\t\t\t<div class=\"json-label\" >where: </div>\n" +
				"\t\t\t\t\t\t\t<span class=\"json-value\" >\n" +
				"\t\t\t\t\t\t\t\t<pre><span id=\"apiCode-${objIndex}-${itemIndex}-where\">${codeWhere}</span></pre>\n" +
				"\t\t\t\t\t\t\t</span>\n" +
				"\t\t\t\t\t\t\t<div class=\"json-copy-button\">\n" +
				"\t\t\t\t\t\t\t\t<button class=\"copy-btn\" onclick=\"copyToClipboard('apiCode-${objIndex}-${itemIndex}-where')\">\n" +
				"\t\t\t\t\t\t\t\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-clipboard\" viewBox=\"0 0 16 16\">\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M5.5 1.5h-1v1h1v-1z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t</svg>\n" +
				"\t\t\t\t\t\t\t\t\tコピー\n" +
				"\t\t\t\t\t\t\t\t</button>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t</div>`;\n" +
				"\t\t\t}\n" +
				"\t\t\tif (codeWherePK != undefined) {\n" +
				"\t\t\t\tapiCodeHtml += `<div style=\"display: flex; flex-direction: row; padding: 10px 0px;\" id=\"wherePK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}\">\n" +
				"\t\t\t\t\t\t\t<div class=\"json-label\" >wherePK: </div>\n" +
				"\t\t\t\t\t\t\t<span class=\"json-value\">\n" +
				"\t\t\t\t\t\t\t\t<pre><span id=\"apiCode-${objIndex}-${itemIndex}-wherePK\">${codeWherePK}</span></pre>\n" +
				"\t\t\t\t\t\t\t</span>\n" +
				"\t\t\t\t\t\t\t<div class=\"json-copy-button\">\n" +
				"\t\t\t\t\t\t\t\t<button class=\"copy-btn\" onclick=\"copyToClipboard('apiCode-${objIndex}-${itemIndex}-wherePK')\">\n" +
				"\t\t\t\t\t\t\t\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-clipboard\" viewBox=\"0 0 16 16\">\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M5.5 1.5h-1v1h1v-1z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t</svg>\n" +
				"\t\t\t\t\t\t\t\t\tコピー\n" +
				"\t\t\t\t\t\t\t\t</button>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t</div>`;\n" +
				"\t\t\t}\n" +
				"\t\t\tif (codeWhereUK != undefined) {\n" +
				"\t\t\t\tapiCodeHtml += `<div style=\"display: flex; flex-direction: row; padding: 10px 0px;\" id=\"whereUK-${objIndex}-${crudType}-${itemIndex}-value-${replaceToCamelCase(apiType)}\">\n" +
				"\t\t\t\t\t\t\t<div class=\"json-label\" >whereUK: </div>\n" +
				"\t\t\t\t\t\t\t<span class=\"json-value\">\n" +
				"\t\t\t\t\t\t\t\t<pre><span id=\"apiCode-${objIndex}-${itemIndex}-whereUK\">${codeWhereUK}</span></pre>\n" +
				"\t\t\t\t\t\t\t</span>\n" +
				"\t\t\t\t\t\t\t<div class=\"json-copy-button\"\t>\n" +
				"\t\t\t\t\t\t\t\t<button class=\"copy-btn\" onclick=\"copyToClipboard('apiCode-${objIndex}-${itemIndex}-whereUK')\">\n" +
				"\t\t\t\t\t\t\t\t\t<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" fill=\"currentColor\" class=\"bi bi-clipboard\" viewBox=\"0 0 16 16\">\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M10 1.5v-1h1a1 1 0 0 1 1 1v1h.5A1.5 1.5 0 0 1 14 3v10a1.5 1.5 0 0 1-1.5 1.5h-9A1.5 1.5 0 0 1 2 13V3A1.5 1.5 0 0 1 3.5 1.5H4v-1a1 1 0 0 1 1-1h1v1h1.5v1H5V2H4v1h8V2h-1v1h-2.5v-1H10z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t\t<path d=\"M5.5 1.5h-1v1h1v-1z\"/>\n" +
				"\t\t\t\t\t\t\t\t\t</svg>\n" +
				"\t\t\t\t\t\t\t\t\tコピー\n" +
				"\t\t\t\t\t\t\t\t</button>\n" +
				"\t\t\t\t\t\t\t</div>\n" +
				"\t\t\t\t\t\t</div>`;\n" +
				"\t\t\t}\n" +
				"\t\t\tapiCodeHtml += `</div>\n" +
				"\t\t\t</div>`;\n" +
				"\t\t\treturn html.replace('{item'+itemIndex+'}', apiCodeHtml);\n" +
				"\t\t}\n" +
				"\t\treturn html.replace('{item'+itemIndex+'}', \"\");\n" +
				"\t}\n" +
				"\t\n" +
				"\tfunction replaceToCamelCase(str) {\n" +
				"\t\treturn str.toLowerCase().replace(/_./g, match => match.charAt(1).toUpperCase());;\n" +
				"\t}\n" +
				"\n" +
				"    function toggleDetails(index) {\n" +
				"        const container = document.getElementById(`json-${index}`);\n" +
				"        const toggleBtn = container.firstElementChild.firstElementChild.firstElementChild;\n" +
				"        const details = document.getElementById(`details-${index}`);\n" +
				"        if (details.classList.contains('hidden')) {\n" +
				"            details.classList.remove('hidden');\n" +
				"            toggleBtn.innerHTML = '▼';\n" +
				"        } else {\n" +
				"            details.classList.add('hidden');\n" +
				"            toggleBtn.innerHTML = '▶';\n" +
				"        }\n" +
				"    }\n" +
				"\t\n" +
				"    function copyToClipboard(elementId) {\n" +
				"        const textToCopy = document.getElementById(elementId).textContent;\n" +
				"        navigator.clipboard.writeText(textToCopy).then(() => {\n" +
				"            showNotification('Copied to clipboard!');\n" +
				"        }).catch(err => {\n" +
				"            console.error('Could not copy text: ', err);\n" +
				"        });\n" +
				"\t\tevent.stopPropagation();\n" +
				"    }\n" +
				"\n" +
				"    function showNotification(message) {\n" +
				"        const notification = document.getElementById('notification');\n" +
				"        notification.textContent = message;\n" +
				"        notification.classList.add('show');\n" +
				"        \n" +
				"        // Hide notification after 2 seconds\n" +
				"        setTimeout(() => {\n" +
				"            notification.classList.remove('show');\n" +
				"        }, 2000);\n" +
				"    }\n" +
				"    function searchJSON() {\n" +
				"        const searchTerm = document.getElementById('searchByDescription').value.toLowerCase();\n" +
				"        const typeFilter = document.getElementById('searchByType').value;\n" +
				"  \t \t currentPage = 1;\n" +
				"        const filteredArray = jsonArray.filter(obj => {\n" +
				"            const descriptionMatch = obj.description.toLowerCase().includes(searchTerm);\n" +
				"            const typeMatch = typeFilter === \"\" || obj.type === typeFilter;\n" +
				"            return descriptionMatch && typeMatch;\n" +
				"        });\n" +
				"        displayJSON(filteredArray);\n" +
				"    }\n" +
				"\t\n" +
				"\n" +
				"    function displayPagination(filteredArray) {\n" +
				"        const paginationElement = document.getElementById('pagination');\n" +
				"        paginationElement.innerHTML = ''; \n" +
				"\n" +
				"        const totalPages = Math.ceil(filteredArray.length / itemsPerPage);\n" +
				"\n" +
				"        const prevBtn = document.createElement('button');\n" +
				"        prevBtn.className = `pagination-btn ${currentPage === 1 ? 'disabled' : ''}`;\n" +
				"        prevBtn.textContent = '前へ';\n" +
				"        prevBtn.onclick = () => changePage(currentPage - 1, filteredArray);\n" +
				"        paginationElement.appendChild(prevBtn);\n" +
				"\n" +
				"        const nextBtn = document.createElement('button');\n" +
				"        nextBtn.className = `pagination-btn ${currentPage === totalPages ? 'disabled' : ''}`;\n" +
				"        nextBtn.textContent = '次へ';\n" +
				"        nextBtn.onclick = () => changePage(currentPage + 1, filteredArray);\n" +
				"        paginationElement.appendChild(nextBtn);\n" +
				"    }\n" +
				"\n" +
				"    function displayPageInfo(filteredArray) {\n" +
				"        const pageInfoElement = document.getElementById('page-info');\n" +
				"        const totalItems = filteredArray.length;\n" +
				"        const start = (currentPage - 1) * itemsPerPage + 1;\n" +
				"        const end = Math.min(currentPage * itemsPerPage, totalItems);\n" +
				"\t\tif (start <= end) {\n" +
				"\t\t\tpageInfoElement.textContent = `${currentPage} / ${Math.ceil(totalItems / itemsPerPage)}ページ（${start}-${end}件 / ${totalItems}件）`;\n" +
				"\t\t} else {\n" +
				"\t\t\tpageInfoElement.textContent = `0 / ${Math.ceil(totalItems / itemsPerPage)}ページ（0-${end}件 / ${totalItems}件）`;\n" +
				"\t\t}\n" +
				"    }\n" +
				"\tfunction filterApiCodes(index) {\n" +
				"\t\tconsole.log(document.getElementById('filterByApi-' + index).value)\n" +
				"\t\tdocument.getElementById('apiCode-' + index).innerHTML = jsonArray[index]['apiCodes'][document.getElementById('filterByApi-' + index).value];\n" +
				"\t\tevent.stopPropagation();\n" +
				"\t}\n" +
				"\n" +
				"    function changePage(newPage, filteredArray) {\n" +
				"        if (newPage < 1 || newPage > Math.ceil(filteredArray.length / itemsPerPage)) return;\n" +
				"        currentPage = newPage;\n" +
				"        displayJSON(filteredArray);\n" +
				"    }\n" +
				"\tfunction changePaginationRange() {\n" +
				"\t\titemsPerPage = document.getElementById('paginationNumber').value;\n" +
				"\t\tcurrentPage=1;\n" +
				"\t\tsearchJSON();\n" +
				"\t}\n" +
				"\tfunction openDialog(event, index, paginatedArray) {\n" +
				"        document.querySelector('.overlay').style.display = 'block';\n" +
				"        document.querySelector('.dialog').style.display = 'block';\n" +
				"        document.querySelector('.dialog').style.transform = 'scale(1)';\n" +
				"\t\treplaceVeInfo(index, paginatedArray);\n" +
				"\t\tresetToTheFirstTab();\n" +
				"\t\tdocument.body.style.overflow = 'hidden';\n" +
				"\t\tevent.stopPropagation();\n" +
				"    }\n" +
				"\n" +
				"    function closeDialog() {\n" +
				"        document.querySelector('.overlay').style.display = 'none';\n" +
				"        document.querySelector('.dialog').style.display = 'none';\n" +
				"\t\tdocument.body.style.overflow = 'auto';\n" +
				"    }\n" +
				"    displayJSON(jsonArray);\n" +
				"\tfunction resetToTheFirstTab() {\n" +
				"\t\ttabs.forEach(tab => tab.classList.remove('active'));\n" +
				"\t\tcontents.forEach(content => content.classList.remove('active'));\n" +
				"\n" +
				"\t\ttabs[0].classList.add('active');\n" +
				"\t\tcontents[0].classList.add('active');\n" +
				"\t}\n" +
				"\t\n" +
				"\tfunction replaceVeInfo(objIndex, paginatedArray) {\n" +
				"\t\tconst ve = paginatedArray[objIndex].virtualEntity;\n" +
				"\t\tdocument.getElementById(\"ve-info-veid\").innerHTML = ve.veId;\n" +
				"\t\tdocument.getElementById(\"ve-info-categoryid\").innerHTML = ve.categoryId;\n" +
				"\t\tdocument.getElementById(\"ve-info-vetype\").innerHTML = ve.veType;\n" +
				"\t\tdocument.getElementById(\"ve-info-displayname\").innerHTML = ve.displayName;\n" +
				"\t\tdocument.getElementById(\"ve-info-categoryName\").innerHTML = ve.categoryName;\n" +
				"\t\tdocument.getElementById(\"ve-info-absolutevecode\").innerHTML = ve.absoluteVirtualColumnCode;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-name\").innerHTML = ve.refEntity.name;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-database\").innerHTML = ve.refEntity.databaseName;\n" +
				"\t\t\n" +
				"\t\tlet html = `\n" +
				"\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>項目名</th>\n" +
				"\t\t\t\t\t<th>表示名</th>\n" +
				"\t\t\t\t\t<th>集約関数</th>\n" +
				"\t\t\t\t\t<th>初期値</th>\n" +
				"\t\t\t\t\t<th>フォーマット</th>\n" +
				"\t\t\t\t\t<th>エンティティ名.項目名</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tve.columns.forEach((obj, index) => {\n" +
				"\t\t\tconst refColumn = obj.refColumn != null ? obj.refColumn : '<span class=\"absolute-ve\">完全仮想項目</span>';\n" +
				"\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t<td>${obj.name}</td>\n" +
				"\t\t\t\t\t<td>${obj.displayName}</td>\n" +
				"\t\t\t\t\t<td>${obj.aggregateFunction}</td>\n" +
				"\t\t\t\t\t<td>${obj.fixedValue}</td>\n" +
				"\t\t\t\t\t<td>${obj.format}</td>\n" +
				"\t\t\t\t\t<td>${refColumn}</td>\n" +
				"\t\t\t\t</tr>`;\n" +
				"\t\t});\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-columns\").innerHTML = html;\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>項目名</th>\n" +
				"\t\t\t\t\t<th>サイズ</th>\n" +
				"\t\t\t\t\t<th>データタイプ</th>\n" +
				"\t\t\t\t\t<th>少数サイズ</th>\n" +
				"\t\t\t\t\t<th>Null可</th>\n" +
				"\t\t\t\t\t<th>プライマリキー</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tve.refEntity.columns.forEach((obj, index) => {\n" +
				"\t\t\tconst isPrimaryKey = obj.primaryKey == true ? '<span class=\"absolute-ve\">PK</span>' : '-';\n" +
				"\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t<td>${obj.name}</td>\n" +
				"\t\t\t\t\t<td>${obj.size}</td>\n" +
				"\t\t\t\t\t<td>${obj.dataType}</td>\n" +
				"\t\t\t\t\t<td>${obj.decimalSize}</td>\n" +
				"\t\t\t\t\t<td>${obj.notNull}</td>\n" +
				"\t\t\t\t\t<td>${isPrimaryKey}</td>\n" +
				"\t\t\t\t</tr>`;\n" +
				"\t\t});\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-columns\").innerHTML = html;\n" +
				"\t\thtml = ``;\n" +
				"\t\tif (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {\n" +
				"\t\t\tve.refEntity.uniqueKeys.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<option>${obj.name}</option>`;\n" +
				"\t\t\t});\n" +
				"\t\t} else {\n" +
				"\t\t\thtml = `<option>-</option>`;\n" +
				"\t\t}\n" +
				"\n" +
				"\t\tdocument.getElementById(\"ve-entity-unique-keys\").innerHTML = html;\n" +
				"\t\tdocument.getElementById(\"ve-entity-unique-keys\").onchange = function() {\n" +
				"\t\t\tswitchRefEntityUniqueKey(objIndex, paginatedArray, document.getElementById(\"ve-entity-unique-keys\").value)\n" +
				"\t\t}\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>項目名</th>\n" +
				"\t\t\t\t\t<th>サイズ</th>\n" +
				"\t\t\t\t\t<th>データタイプ</th>\n" +
				"\t\t\t\t\t<th>少数サイズ</th>\n" +
				"\t\t\t\t\t<th>Null可</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {\n" +
				"\t\t\tve.refEntity.uniqueKeys[0].uniqueKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\tconst isPrimaryKey = obj.primaryKey == true ? '<span class=\"absolute-ve\">PK</span>' : '-';\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.name}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.size}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.dataType}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.decimalSize}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.notNull}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-unique-keys-columns\").innerHTML = html;\n" +
				"\t\thtml = ``;\n" +
				"\t\tif (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {\n" +
				"\t\t\tve.refEntity.foreignKeys.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<option>${obj.name}</option>`;\n" +
				"\t\t\t});\n" +
				"\t\t} else {\n" +
				"\t\t\thtml = `<option>-</option>`;\n" +
				"\t\t}\n" +
				"\t\tdocument.getElementById(\"ve-entity-foreign-keys\").innerHTML = html;\n" +
				"\t\t\n" +
				"\t\tdocument.getElementById(\"ve-entity-foreign-keys\").onchange = function() {\n" +
				"\t\t\tswitchRefEntityForeignKey(objIndex, paginatedArray, document.getElementById(\"ve-entity-foreign-keys\").value)\n" +
				"\t\t}\n" +
				"\t\tif (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-ref\").innerHTML = ve.refEntity.foreignKeys[0].referenceEntity.name;\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-join-type\").innerHTML = ve.refEntity.foreignKeys[0].joinType;\n" +
				"\t\t} else {\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-ref\").innerHTML = \"-\";\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-join-type\").innerHTML = \"-\";\n" +
				"\t\t}\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>参照先項目</th>\n" +
				"\t\t\t\t\t<th>参照元項目</th>\n" +
				"\t\t\t\t\t<th>固定値</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {\n" +
				"\t\t\tve.refEntity.foreignKeys[0].foreignKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.referenceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.sourceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.constValue}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-columns\").innerHTML = html;\n" +
				"\t\tif (ve.absoluteVirtualColumnCode == \"-\") {\n" +
				"\t\t\tdocument.getElementById(\"copy-absolute-ve-btn\").style.display = \"none\";\n" +
				"\t\t} else {\n" +
				"\t\t\tdocument.getElementById(\"copy-absolute-ve-btn\").style.display = \"block\";\n" +
				"\t\t}\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>仮想項目</th>\n" +
				"\t\t\t\t\t<th>比較オペレーター</th>\n" +
				"\t\t\t\t\t<th>フィルター値</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.filterConditions != undefined) {\n" +
				"\t\t\tve.filterConditions.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.column}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.comparisonOperator}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.filterValue}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-filter-conditions\").innerHTML = html;\n" +
				"\t\t\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>仮想項目</th>\n" +
				"\t\t\t\t\t<th>比較オペレーター</th>\n" +
				"\t\t\t\t\t<th>オプション</th>\n" +
				"\t\t\t\t\t<th>スペース</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.searchConditions != undefined) {\n" +
				"\t\t\tve.searchConditions.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.column}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.comparisonOperator}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.optional ? 'Option' : 'Required [IS NULL]'}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.trim ? 'Trim' : 'No Delete'}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-search-conditions\").innerHTML = html;\n" +
				"\t\t\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>仮想項目</th>\n" +
				"\t\t\t\t\t<th>タイプ</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.sortConditions) {\n" +
				"\t\t\tve.sortConditions.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.sortColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.sortMode}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-sort-columns\").innerHTML = html;\n" +
				"\t\t\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>仮想項目</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.groupConditions != undefined) {\n" +
				"\t\t\tve.groupConditions.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.groupColumn}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-group-columns\").innerHTML = html;\n" +
				"\t\t\n" +
				"\t\tif (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {\n" +
				"\t\t\tve.foreignKeys.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<option>${obj.name}</option>`;\n" +
				"\t\t\t});\n" +
				"\t\t} else {\n" +
				"\t\t\thtml = `<option>-</option>`;\n" +
				"\t\t}\n" +
				"\t\tdocument.getElementById(\"ve-foreign-keys\").innerHTML = html;\n" +
				"\t\tdocument.getElementById(\"ve-foreign-keys\").onchange = function() {\n" +
				"\t\t\tswitchForeignKey(objIndex, paginatedArray, document.getElementById(\"ve-foreign-keys\").value)\n" +
				"\t\t}\n" +
				"\t\tif (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-ref\").innerHTML = ve.foreignKeys[0].referenceEntity.name;\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-join-type\").innerHTML = ve.foreignKeys[0].joinType;\n" +
				"\t\t} else {\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-ref\").innerHTML = \"-\";\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-join-type\").innerHTML = \"-\";\n" +
				"\t\t}\n" +
				"\t\thtml = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>参照先項目</th>\n" +
				"\t\t\t\t\t<th>参照元項目</th>\n" +
				"\t\t\t\t\t<th>固定値</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {\n" +
				"\t\t\tve.foreignKeys[0].foreignKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.referenceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.sourceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.constValue}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-foreign-keys-columns\").innerHTML = html;\n" +
				"\t\tcheckAllTableContainerOverflow();\n" +
				"\t}\n" +
				"\twindow.addEventListener('keydown', (event) => {\n" +
				"      if (event.key === 'Escape') {\n" +
				"        closeDialog();\n" +
				"      }\n" +
				"    });\n" +
				"\t\n" +
				"\t\n" +
				"\tconst tabs = document.querySelectorAll('.tab');\n" +
				"\tconst contents = document.querySelectorAll('.tab-content');\n" +
				"\n" +
				"\tfunction switchTab(event) {\n" +
				"\t\tconst targetId = event.target.getAttribute('data-target');\n" +
				"\t\ttabs.forEach(tab => tab.classList.remove('active'));\n" +
				"\t\tcontents.forEach(content => content.classList.remove('active'));\n" +
				"\n" +
				"\t\tevent.target.classList.add('active');\n" +
				"\t\tdocument.getElementById(targetId).classList.add('active');\n" +
				"\t\tcheckAllTableContainerOverflow();\n" +
				"\t}\n" +
				"\n" +
				"\ttabs.forEach(tab => {\n" +
				"\t\ttab.addEventListener('click', switchTab);\n" +
				"\t});\n" +
				"\n" +
				"\ttabs[0].classList.add('active');\n" +
				"\tcontents[0].classList.add('active');\n" +
				"\tfunction switchForeignKey(objIndex, paginatedArray, name) {\n" +
				"\t\tconst ve = paginatedArray[objIndex].virtualEntity;\n" +
				"\t\tlet html = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>参照先項目</th>\n" +
				"\t\t\t\t\t<th>参照元項目</th>\n" +
				"\t\t\t\t\t<th>固定値</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.foreignKeys != undefined && ve.foreignKeys.length > 0) {\n" +
				"\t\t\tconst filteredFK = ve.foreignKeys.filter(fk => fk.name === name)[0];\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-ref\").innerHTML = filteredFK.referenceEntity.name;\n" +
				"\t\t\tdocument.getElementById(\"ve-info-foreign-keys-join-type\").innerHTML = filteredFK.joinType;\n" +
				"\t\t\tfilteredFK.foreignKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.referenceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.sourceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.constValue}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-foreign-keys-columns\").innerHTML = html;\n" +
				"\t}\n" +
				"\tfunction switchRefEntityForeignKey(objIndex, paginatedArray, name) {\n" +
				"\t\tconst ve = paginatedArray[objIndex].virtualEntity;\n" +
				"\t\tlet html = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>参照先項目</th>\n" +
				"\t\t\t\t\t<th>参照元項目</th>\n" +
				"\t\t\t\t\t<th>固定値</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.refEntity.foreignKeys != undefined && ve.refEntity.foreignKeys.length > 0) {\n" +
				"\t\t\tconst filteredFK = ve.refEntity.foreignKeys.filter(fk => fk.name === name)[0];\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-ref\").innerHTML = filteredFK.referenceEntity.name;\n" +
				"\t\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-join-type\").innerHTML = filteredFK.joinType;\n" +
				"\t\t\tfilteredFK.foreignKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.referenceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.sourceColumn}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.constValue}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-foreign-keys-columns\").innerHTML = html;\n" +
				"\t}\n" +
				"\tfunction switchRefEntityUniqueKey(objIndex, paginatedArray, name) {\n" +
				"\t\tconst ve = paginatedArray[objIndex].virtualEntity;\n" +
				"\t\tlet html = `\n" +
				"\t\t\t<table>\n" +
				"\t\t\t<thead>\n" +
				"\t\t\t\t<tr>\n" +
				"\t\t\t\t\t<th>項目名</th>\n" +
				"\t\t\t\t\t<th>データタイプ</th>\n" +
				"\t\t\t\t\t<th>サイズ</th>\n" +
				"\t\t\t\t\t<th>少数サイズ</th>\n" +
				"\t\t\t\t\t<th>Null可</th>\n" +
				"\t\t\t\t</tr>\n" +
				"\t\t\t</thead>\n" +
				"\t\t\t<tbody>`;\n" +
				"\t\tif (ve.refEntity.uniqueKeys != undefined && ve.refEntity.uniqueKeys.length > 0) {\n" +
				"\t\t\tconst filteredUK = ve.refEntity.uniqueKeys.filter(uk => uk.name === name)[0];\n" +
				"\t\t\tfilteredUK.uniqueKeyColumns.forEach((obj, index) => {\n" +
				"\t\t\t\tconst isPrimaryKey = obj.primaryKey == true ? '<span class=\"absolute-ve\">PK</span>' : '-';\n" +
				"\t\t\t\thtml += `<tr>\n" +
				"\t\t\t\t\t\t<td>${obj.name}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.dataType}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.size}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.decimalSize}</td>\n" +
				"\t\t\t\t\t\t<td>${obj.notNull}</td>\n" +
				"\t\t\t\t\t</tr>`;\n" +
				"\t\t\t});\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\thtml += `\n" +
				"\t\t\t</tbody>\n" +
				"\t\t</table>`;\n" +
				"\t\tdocument.getElementById(\"ve-info-entity-unique-keys-columns\").innerHTML = html;\n" +
				"\t}\n" +
				"\tfunction checkOverflow(resizableDiv) {\n" +
				"\t  const isOverflowing = resizableDiv.scrollHeight > resizableDiv.clientHeight;\n" +
				"\t  if (isOverflowing) {\n" +
				"\t\tresizableDiv.style.resize = 'vertical';\n" +
				"\t  } else {\n" +
				"\t\tresizableDiv.style.resize = 'none';\n" +
				"\t  }\n" +
				"\t}\n" +
				"\twindow.addEventListener('load', function() {\n" +
				"\t\tcheckAllTableContainerOverflow();\n" +
				"\t});\n" +
				"\tfunction checkAllTableContainerOverflow() {\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-entity-foreign-keys-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-entity-unique-keys-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-entity-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-group-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-sort-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-search-conditions-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-filter-conditions-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-foreign-keys-columns-container\"));\n" +
				"\t\tcheckOverflow(document.getElementById(\"ve-info-columns-container\"));\n" +
				"\t}\n" +
				"\twindow.addEventListener('resize', function() {\n" +
				"\t\tcheckAllTableContainerOverflow();\n" +
				"\t});\n" +
				"\n" +
				"</script>\n" +
				"\n" +
				"</body>\n" +
				"</html>\n";
	}
}
