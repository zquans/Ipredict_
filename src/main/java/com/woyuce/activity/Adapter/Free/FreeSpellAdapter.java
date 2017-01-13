package com.woyuce.activity.Adapter.Free;

import java.util.ArrayList;
import java.util.List;

import com.woyuce.activity.Bean.Free.FreeSpellBean;
import com.woyuce.activity.R;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class FreeSpellAdapter extends BaseAdapter {

	private List<FreeSpellBean> mDatalist;
	private LayoutInflater mLayoutnflate;
	private boolean flag = false;

	private List<String> answerList = new ArrayList<>();

	public FreeSpellAdapter(Context context, List<FreeSpellBean> list, boolean flag, List<String> answerList) {
		this.mDatalist = list;
		mLayoutnflate = LayoutInflater.from(context);
		this.answerList = answerList;
		this.flag = flag;
	}

	@Override
	public int getCount() {
		return mDatalist.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatalist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Viewhold viewhold;
		if (convertView == null) {
			viewhold = new Viewhold();
			convertView = mLayoutnflate.inflate(R.layout.listitem_freecheckspell, null);
			viewhold.mTxtnum = (TextView) convertView.findViewById(R.id.txt_checkspell_num);
			viewhold.mAnswer = (TextView) convertView.findViewById(R.id.txt_checkspell_answer);
			viewhold.mSpell = (TextView) convertView.findViewById(R.id.txt_checkspell_showspell);
			viewhold.mEdtspell = (EditText) convertView.findViewById(R.id.edt_checkspell_spell);
			convertView.setTag(viewhold);
		} else {
			viewhold = (Viewhold) convertView.getTag();
		}

		// EditText����Tag
		final FreeSpellBean bean = mDatalist.get(position);
		viewhold.mEdtspell.setTag(bean);
		
//		//ȡ��������ȡ���㣬���⽹�㸴��
//		viewhold.mEdtspell.clearFocus();

		viewhold.mTxtnum.setText(mDatalist.get(position).num);
		viewhold.mSpell.setText(mDatalist.get(position).spell);
		viewhold.mAnswer.setText(answerList.get(position));
		viewhold.mEdtspell.setHint("");

		// Edit���ݼ���
		viewhold.mEdtspell.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable editable) {
				FreeSpellBean bean = (FreeSpellBean) viewhold.mEdtspell.getTag();
				bean.spell = editable.toString();
			}

			public void beforeTextChanged(CharSequence text, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence text, int start, int before, int count) {
			}
		});

		// �󲿷�����£�Adapter������if������else
		if (!bean.spell.equals("")) {
			viewhold.mEdtspell.setText(bean.spell);
		} else {
			viewhold.mEdtspell.setText("");
		}

		//���öԴ����ɫ
		if (answerList.get(position).toString().toLowerCase().trim().equals(mDatalist.get(position).spell.toString().toLowerCase().trim())) {
			viewhold.mSpell.setTextColor(Color.parseColor("#000000"));
		} else {
			viewhold.mSpell.setTextColor(Color.parseColor("#ff0000"));
		}

		// �жϱ�ʶ
		if (flag == true) {
			viewhold.mAnswer.setVisibility(View.VISIBLE);
			viewhold.mSpell.setVisibility(View.VISIBLE);
			viewhold.mEdtspell.setVisibility(View.GONE);
//			viewhold.mEdtspell.setFocusable(false);
		} else {
			viewhold.mAnswer.setVisibility(View.GONE);
			// viewhold.mEdtspell.setFocusable(true);
		}
		return convertView;
	}

	public class Viewhold {
		public TextView mTxtnum, mSpell, mAnswer;
		public EditText mEdtspell;
	}

	public ArrayList<FreeSpellBean> returnSpellList(){
		return (ArrayList<FreeSpellBean>) mDatalist;
	}
}