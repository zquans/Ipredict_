package com.woyuce.activity.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.woyuce.activity.R;
import com.woyuce.activity.Utils.LogUtil;

public class Fragment_StoreGoods_Two extends Fragment {

    private LinearLayout mLinearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storegoods_two, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLinearLayout = (LinearLayout) view.findViewById(R.id.ll_fragment_goods_two);

        //真实数据
        LogUtil.i("mImgList = " + getArguments().getString("mImgList"));
        //伪数据
        String a = "<p><img src=\"http://www.w3school.com.cn/i/eg_tulip.jpg\" title=\"集训营抵用券.jpg\" \n" +
                "alt=\"集训营抵用券.jpg\"/></p>\n" +
                "\n" +
                "<p><img src=\"http://www.w3school.com.cn/i/eg_tulip.jpg\" alt=\"6359970573303732439200716.jpg\" width=\"818\" height=\"2347\" style=\"width: 818px; height: 2347px;\"/></p>";

//        TextView mTxt = (TextView) view.findViewById(R.id.text_test);
//        mTxt.setText(Html.fromHtml(getArguments().getString("mImgList"), new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String source) {
//                Drawable drawable = null;
//                URL url;
//                try {
//                    url = new URL(source);
//                    drawable = Drawable.createFromStream(url.openStream(), "");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//                return drawable;
//            }
//        }, new Html.TagHandler() {
//            @Override
//            public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//                ToastUtil.showMessage(getActivity(), tag);
//            }
//        }));

        WebView mWeb = (WebView) view.findViewById(R.id.web_test);
        mWeb.getSettings().setJavaScriptEnabled(true);
//        mWeb.loadData(getFormatHtml(a), "text/html; charset=UTF-8", null);
        mWeb.loadUrl(getArguments().getString("mImgList"));

//        for (int i = 0; i < 3; i++) {
//            ImageView mImg = new ImageView(getActivity());
//            mImg.setBackgroundResource(R.mipmap.background_music);
//            mLinearLayout.addView(mImg);
//        }
    }

    /**
     * 转义Html，可含图
     *
     * @param content
     * @return
     */
    private String getFormatHtml(String content) {
        //如果没有img图像标签，可以不做任何处理
        if (!content.contains("<img")) {
            return content;
        }
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<html>");
        strBuilder.append("<head>");
        strBuilder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
        strBuilder.append("<title>无标题文档</title>");
        strBuilder.append("<script type=\"text/javascript\">");
        strBuilder.append("function aaa() {");
        strBuilder.append("var imgTags = document.getElementsByTagName(\"img\");");

        strBuilder.append("var len = imgTags.length;");
        strBuilder.append("for(var i=0;i<len;i++) {");
        strBuilder.append("imgTags.item(i).onclick = function() {");
        strBuilder.append(" window.open(this.src,null,null,null);");
        strBuilder.append("};");
        strBuilder.append("}");

        strBuilder.append("}");
        strBuilder.append("</script>");
        strBuilder.append("<style type=\"text/css\">");
        strBuilder.append("img{ max-width:100%;}");
        strBuilder.append("div{ width:auto; height:auto;}");
        strBuilder.append("</style>");
        strBuilder.append("</head>");

        strBuilder.append("<body onload=\"aaa();\">");

        strBuilder.append("<div>");
        strBuilder.append(content.replaceAll("style=", "")); //此处为去掉原始属性。如果想去掉指定标签的style属性，此处需要特殊处理。
        strBuilder.append("</div>");
        strBuilder.append("</body>");
        strBuilder.append("</html>");
        return strBuilder.toString();
    }
}