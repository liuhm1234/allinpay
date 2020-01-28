package com.pack.iso8583;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pack.iso8583.codec.FieldAttribute;
import com.pack.iso8583.codec.FieldCodecFactory;
import com.pack.iso8583.codec.FieldCodecManager;
import com.pack.iso8583.codec.FieldType;
import com.pack.iso8583.codec.IFieldCodec;

public class ISO8583ConfigParser {
	private static final String TAG = ISO8583ConfigParser.class.getSimpleName();
	private static Pattern PATTERN_NUM = Pattern.compile("[0-9]+");
	private static Map<Pattern, FieldType> sFieldMap = new HashMap();

	static {
		sFieldMap.put(Pattern.compile("N[0-9]+"), FieldType.N_FIX);
		sFieldMap.put(Pattern.compile("N\\.{2}[0-9]+"), FieldType.N_LLVAR);
		sFieldMap.put(Pattern.compile("N\\.{3}[0-9]+"), FieldType.N_LLLVAR);
		sFieldMap
				.put(Pattern.compile("(A|AN|ANS|AS)[0-9]+"), FieldType.ANS_FIX);
		sFieldMap.put(Pattern.compile("(A|AN|ANS|AS)\\.{2}[0-9]+"),
				FieldType.ANS_LLVAR);
		sFieldMap.put(Pattern.compile("(A|AN|ANS|AS)\\.{3}[0-9]+"),
				FieldType.ANS_LLLVAR);
		sFieldMap.put(Pattern.compile("B[0-9]+"), FieldType.B_FIX);
		sFieldMap.put(Pattern.compile("B\\.{2}[0-9]+"), FieldType.B_LLVAR);
		sFieldMap.put(Pattern.compile("B\\.{3}[0-9]+"), FieldType.B_LLLVAR);
		sFieldMap.put(Pattern.compile("Z[0-9]+"), FieldType.Z_FIX);
		sFieldMap.put(Pattern.compile("Z\\.{2}[0-9]+"), FieldType.Z_LLVAR);
		sFieldMap.put(Pattern.compile("Z\\.{3}[0-9]+"), FieldType.Z_LLLVAR);
	}

	public static FieldCodecManager load(ClassLoader loader, String fileName) {
		FieldCodecManager fieldCodecManager = new FieldCodecManager();

		try {
			InputStream input = loader.getResourceAsStream(fileName);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(input);
			Element root = document.getDocumentElement();
			boolean isCompress = root.getAttribute("compress").equals("true");
			NodeList fields = root.getElementsByTagName("field");

			for (int i = 0; i < fields.getLength(); ++i) {
				Element field = (Element) fields.item(i);
				int id = Integer.parseInt(field.getAttribute("id"));
				FieldAttribute attr = parseTypeConfig(field
						.getAttribute("type"));
				attr.compress = isCompress;
				attr.align = (char) (field.getAttribute("align")
						.equalsIgnoreCase("right") ? 'R' : 'L');
				IFieldCodec codec = FieldCodecFactory.createFieldCodec(attr);
				if (codec != null) {
					fieldCodecManager.add(id, codec);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fieldCodecManager;
	}

	private static FieldAttribute parseTypeConfig(String input) {
		FieldAttribute attribute = new FieldAttribute();
		attribute.type = FieldType.UNKNOWN;
		Set patterns = sFieldMap.keySet();
		Iterator<Pattern> iterator = patterns.iterator();

		while (iterator.hasNext()) {
			Pattern matcher = iterator.next();
			if (matcher.matcher(input).matches()) {
				attribute.type = (FieldType) sFieldMap.get(matcher);
			}
		}

		if (attribute.type == FieldType.UNKNOWN) {
			throw new IllegalStateException("配置文件错误");
		} else {
			Matcher matcher1 = PATTERN_NUM.matcher(input);
			if (matcher1.find()) {
				attribute.maxlen = Integer.parseInt(matcher1.group());
			}

			return attribute;
		}
	}
}
