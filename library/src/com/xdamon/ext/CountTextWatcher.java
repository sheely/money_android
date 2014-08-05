package com.xdamon.ext;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class CountTextWatcher implements TextWatcher {

	EditText inputText;
	TextView showView;
	int maxLength = Integer.MAX_VALUE;

	/**
	 * 
	 * @param inputEditText
	 *            输入框
	 * @param showTextView
	 *            输入限制显示
	 * @param maxInputLength
	 *            最大可输入数
	 */
	public CountTextWatcher(EditText inputEditText, TextView showTextView, int maxInputLength) {
		this.inputText = inputEditText;
		this.showView = showTextView;
		this.maxLength = maxInputLength;

		InputFilter[] inputFilter = new InputFilter[1];
		inputFilter[0] = new InputFilter.LengthFilter(maxLength);
		inputText.setFilters(inputFilter);

		showView.setText("最多输入" + maxInputLength + "字符");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String limitText = "最多输入" + maxLength + "字符";
		if (!TextUtils.isEmpty(s)) {
			int inputNum = s.length();
			limitText = "还可输入" + (maxLength - inputNum) + "字符";
		}
		showView.setText(limitText);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

}
