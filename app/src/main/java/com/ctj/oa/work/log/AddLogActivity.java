package com.ctj.oa.work.log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ctj.oa.Constants;
import com.ctj.oa.R;
import com.ctj.oa.activity.BaseActivity;
import com.ctj.oa.model.UserInfo;
import com.ctj.oa.model.event.EventRefresh;
import com.ctj.oa.model.netmodel.NetBaseBean;
import com.ctj.oa.model.work.log.LogTemplate;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.imageloader.ImageLoader;
import com.ctj.oa.widgets.TitleBar;
import com.ctj.oa.work.ChooseUserActivity;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddLogActivity extends BaseActivity {
    private String TAG = AddLogActivity.this.getClass().getSimpleName();
    private TitleBar titleBar;
    private int id;
    private View footerView;
    private RecyclerView rv, rv0;
    private TimePickerDialog pickerDialog;
    private List<UserInfo> users = new ArrayList<UserInfo>();
    private LogTemplate logTemplate;
    private String name;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_log;
    }

    public static void goTo(Context context, int id, String name) {
        Intent intent = new Intent(context, AddLogActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
        name = intent.getStringExtra("name");
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        if (!TextUtils.isEmpty(name)) {
            titleBar.setCenterText(name);
        }
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logTemplate.makeData(new LogTemplate.Check() {
                    @Override
                    public void Fail(String title) {
                        Toast("'" + title + "' 这一项没填写完整");
                    }

                    @Override
                    public void Success(String date) {
                        addApplyApprove(date);
                    }
                });
            }
        });
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        footerView = LayoutInflater.from(this).inflate(R.layout.item_log_template_footer, (ViewGroup) rv.getParent(), false);
        adapter.addFooterView(footerView);
        footerView.findViewById(R.id.ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseUserActivity.goTo(bContext, users, TAG);
            }
        });
        rv0 = (RecyclerView) footerView.findViewById(R.id.rv0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv0.setLayoutManager(linearLayoutManager);
        rv0.setAdapter(userAdapter);
        getLogTemplateInfo();
    }

    private void getLogTemplateInfo() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_LOG_TEMPLATE_INFO);
        request.add("id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    logTemplate = netBaseBean.parseObject(LogTemplate.class);
                    adapter.setNewData(logTemplate.getRules());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true, "");
    }

    private void addApplyApprove(String data_field) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_LOG);
        request.add("id", id);
        if (users != null && users.size() > 0) {
            String ids = "";
            for (UserInfo info : users) {
                ids = ids + info.getId() + ",";
            }
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.length() - 1);
            }
            request.add("share_user_id", ids);
        } else {
            request.add("share_user_id", 0);
        }
        request.add("data_field", data_field.toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                Toast(netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }


    private BaseQuickAdapter<LogTemplate.Item, BaseViewHolder> adapter = new BaseQuickAdapter<LogTemplate.Item, BaseViewHolder>(new ArrayList<LogTemplate.Item>()) {
        @Override
        protected void convert(final BaseViewHolder helper, final LogTemplate.Item item) {
            if (helper.getView(R.id.tv) != null) {
                helper.setText(R.id.tv, item.title_field + (item.required_field == 1 ? "  (必填)" : ""));
            }
            /*helper.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T.showShort(AddApprovalActivity.this, item.value + item.values.size());
                }
            });*/
            switch (helper.getItemViewType()) {
                case 1:
                case 2:
                case 3:
                    final EditText edt = helper.getView(R.id.edt);
                    if (edt.getTag() instanceof TextWatcher) {
                        edt.removeTextChangedListener((TextWatcher) edt.getTag());
                    }
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        edt.setHint(item.tip_field[0]);
                    }
                    //Logger.e("p+" + helper.getAdapterPosition() + item.value);
                    if (TextUtils.isEmpty(item.value)) {
                        edt.setText("");
                    } else {
                        edt.setText(item.value);
                    }
                    TextWatcher watcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            item.value = s.toString();
                        }
                    };
                    edt.addTextChangedListener(watcher);
                    edt.setTag(watcher);
                    break;
                case 4:
                    RadioGroup radioGroup = helper.getView(R.id.rg);
                    radioGroup.setOnCheckedChangeListener(null);
                    radioGroup.removeAllViews();
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        for (String text : item.tip_field) {
                            RadioButton radioButton = new RadioButton(bContext);
                            radioButton.setText(text);
                            radioGroup.addView(radioButton);
                            if (!TextUtils.isEmpty(item.value) && text.equals(item.value)) {
                                radioButton.setChecked(true);
                            } else {
                                radioButton.setChecked(false);
                            }
                        }
                    }
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                            item.value = ((RadioButton) group.findViewById(checkedId)).getText().toString();
                        }
                    });
                    break;
                case 5:
                    LinearLayout viewGroup = helper.getView(R.id.ll);
                    viewGroup.removeAllViews();
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        for (String text : item.tip_field) {
                            CheckBox checkBox = new CheckBox(bContext);
                            checkBox.setText(text);
                            viewGroup.addView(checkBox);
                            if (item.values != null && item.values.size() > 0 && item.values.contains(text)) {
                                checkBox.setChecked(true);
                            }
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    String textvalue = ((CheckBox) buttonView).getText().toString();
                                    if (isChecked) {
                                        item.values.add(textvalue);
                                    } else {
                                        item.values.remove(textvalue);
                                    }
                                }
                            });
                        }
                    }
                    break;
                case 6:
                    Spinner spinner = helper.getView(R.id.spinner);
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        spinner.setAdapter(new ArrayAdapter<String>(bContext, android.R.layout.simple_spinner_item, item.tip_field));
                        if (item.select_position != -1) {
                            spinner.setSelection(item.select_position);
                        }
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String str = parent.getItemAtPosition(position).toString();
                                item.value = str;
                                item.select_position = position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                item.value = "";
                                item.select_position = -1;
                            }
                        });
                    }
                    break;
                case 7:
                    final TextView tv_time = helper.getView(R.id.tv_time);
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        updateTimeUI(tv_time, item.tip_field[0], item.value);
                        tv_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                creatTimeDialog(new OnDateSetListener() {
                                    @Override
                                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                        try {
                                            item.value = DateUtils.tenLongToString(millseconds / 1000, item.tip_field[0].equals("2") ? DateUtils.yyyyMMDD : DateUtils.DB_DATA_FORMAT);
                                            updateTimeUI(tv_time, item.tip_field[0], item.value);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, item.tip_field[0]);
                            }
                        });
                    }
                    break;
                case 8:

                    final TextView tv_time1 = helper.getView(R.id.tv_time1);
                    final TextView tv_time2 = helper.getView(R.id.tv_time2);
                    if (item.tip_field != null && item.tip_field.length > 0) {
                        if (item.values.size() != 2) {
                            item.values.clear();
                            item.values.add("");
                            item.values.add("");
                        }
                        updateTimeUI(tv_time1, tv_time2, item.values);
                        tv_time1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                creatTimeDialog(new OnDateSetListener() {
                                    @Override
                                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                        try {
                                            item.values.remove(0);
                                            item.values.add(0, DateUtils.tenLongToString(millseconds / 1000, item.tip_field[0].equals("2") ? DateUtils.yyyyMMDD : DateUtils.DB_DATA_FORMAT));
                                            updateTimeUI(tv_time1, tv_time2, item.values);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, item.tip_field[0]);
                            }
                        });
                        tv_time2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                creatTimeDialog(new OnDateSetListener() {
                                    @Override
                                    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
                                        try {
                                            item.values.remove(1);
                                            item.values.add(1, DateUtils.tenLongToString(millseconds / 1000, item.tip_field[0].equals("2") ? DateUtils.yyyyMMDD : DateUtils.DB_DATA_FORMAT));
                                            updateTimeUI(tv_time1, tv_time2, item.values);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, item.tip_field[0]);
                            }
                        });
                    }
                    break;
            }

        }

        private void updateTimeUI(TextView tv1, TextView tv2, List<String> list) {
            try {
                if (list == null || list.size() != 2) {
                    return;
                }
                if (TextUtils.isEmpty(list.get(0)) || list.get(0).equals("0")) {
                    tv1.setText("未填写");
                } else {
                    tv1.setText(list.get(0));
                }
                if (TextUtils.isEmpty(list.get(1)) || list.get(1).equals("0")) {
                    tv2.setText("未填写");
                } else {
                    tv2.setText(list.get(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void updateTimeUI(TextView tv, String type, String value) {
            try {
                if (TextUtils.isEmpty(value) || value.equals("0")) {
                    tv.setText("未填写");
                } else {
                    tv.setText(value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    //单行文本
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_single_text, parent));
                case 2:
                    //多行文本
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_multiple_text, parent));
                case 3:
                    //数字文本
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_num_text, parent));
                case 4:
                    //单选框
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_radio_group, parent));
                case 5:
                    //复选框
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_checkbox_group, parent));
                case 6:
                    //下拉菜单
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_spinner, parent));
                case 7:
                    //日期
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_single_time, parent));
                case 8:
                    //日期区间
                    return new BaseViewHolder(this.getItemView(R.layout.item_approva_template_multiple_time, parent));
                default:
                    return null;
            }
        }

        @Override
        protected int getDefItemViewType(int position) {

            return getItem(position).type_field;
        }
    };
    private BaseQuickAdapter<UserInfo, BaseViewHolder> userAdapter = new BaseQuickAdapter<UserInfo, BaseViewHolder>(R.layout.item_user) {
        @Override
        protected void convert(BaseViewHolder helper, UserInfo item) {
            helper.setText(R.id.tv, item.getNickname());
            if (TextUtils.isEmpty(item.getPortrait())) {
                helper.setVisible(R.id.imv, false);
                helper.setVisible(R.id.tv0, true);
                try {
                    helper.setText(R.id.tv0, item.getNickname().substring(0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ImageLoader.loadHeadImage(bContext, item.getPortrait(), ((ImageView) helper.getView(R.id.imv)), 2);
                helper.setVisible(R.id.imv, true);
                helper.setVisible(R.id.tv0, false);
            }
        }

    };

    private void creatTimeDialog(OnDateSetListener listener, String type) {
        if (listener == null) {
            return;
        }
        long Years = 100L * 365 * 1000 * 60 * 60 * 24L;
        TimePickerDialog.Builder build = new TimePickerDialog.Builder()
                .setCallBack(listener)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis()-Years)
                .setMaxMillseconds(System.currentTimeMillis() + Years)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(bContext, R.color.super_blue2))
                .setWheelItemTextNormalColor(ContextCompat.getColor(bContext, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(bContext, R.color.super_blue))
                .setWheelItemTextSize(12);
        if (!TextUtils.isEmpty(type) && type.equals("2")) {
            build.setType(Type.YEAR_MONTH_DAY);
        } else {
            build.setType(Type.ALL);
        }
        pickerDialog = build.build();
        pickerDialog.show(getSupportFragmentManager(), type);
    }

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG)) {
            if (e.getData() != null) {
                users = (List<UserInfo>) e.getData();
                userAdapter.setNewData(users);
            }
        }
    }
}
