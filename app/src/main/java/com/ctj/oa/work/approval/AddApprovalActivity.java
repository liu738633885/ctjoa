package com.ctj.oa.work.approval;

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
import com.ctj.oa.model.work.approve.ApproveTemplate;
import com.ctj.oa.net.CallServer;
import com.ctj.oa.net.HttpListenerCallback;
import com.ctj.oa.net.NetBaseRequest;
import com.ctj.oa.net.RequsetFactory;
import com.ctj.oa.utils.manager.UserManager;
import com.ctj.oa.widgets.TitleBar;
import com.ctj.oa.work.ChooseUserActivity;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lewis.utils.DateUtils;
import com.lewis.utils.T;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class AddApprovalActivity extends BaseActivity {
    private String TAG = AddApprovalActivity.this.getClass().getSimpleName();
    private RecyclerView rv;
    private View headView, footerView;
    private int id;
    private TextView tv_title, tv_desc, tv_user;
    private TimePickerDialog pickerDialog;
    private TitleBar titleBar;
    private ApproveTemplate detail;
    private String approve_inner_id;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_approva;
    }

    public static void goTo(Context context, int id) {
        Intent intent = new Intent(context, AddApprovalActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    protected void handleIntent(Intent intent) {
        id = intent.getIntExtra("id", 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        titleBar = (TitleBar) findViewById(R.id.titleBar);
        titleBar.getRightGroup().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.makeData(new ApproveTemplate.Check() {
                    @Override
                    public void Fail(String title) {
                        T.showShort(AddApprovalActivity.this, "'" + title + "' 这一项没填写完整");
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
        headView = LayoutInflater.from(this).inflate(R.layout.item_approva_template_title, rv, false);
        tv_title = (TextView) headView.findViewById(R.id.tv_title);
        tv_desc = (TextView) headView.findViewById(R.id.tv_desc);
        adapter.addHeaderView(headView);
        footerView = LayoutInflater.from(this).inflate(R.layout.item_approva_template_footer, rv, false);
        tv_user = (TextView) footerView.findViewById(R.id.tv_user);
        adapter.addFooterView(footerView);
        getApproveTemplateDetail();
    }

    private void addApplyApprove(String data_field) {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.ADD_APPLY_APPROVE);
        request.add("id", id);
        if (TextUtils.isEmpty(approve_inner_id)) {
            T.showShort(AddApprovalActivity.this, "必须填写个审批人");
            return;
        }
        if (approve_inner_id.equals(UserManager.getId())) {
            T.showShort(AddApprovalActivity.this, "审批者不能为自己");
            return;
        }
        request.add("approve_inner_id", approve_inner_id);
        request.add("data_field", data_field.toString());
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                T.showShort(AddApprovalActivity.this, netBaseBean.getMessage());
                if (netBaseBean.isSuccess()) {
                    finish();
                } else {

                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);

    }

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
                .setThemeColor(ContextCompat.getColor(AddApprovalActivity.this, R.color.super_blue2))
                .setWheelItemTextNormalColor(ContextCompat.getColor(AddApprovalActivity.this, R.color.gray01))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(AddApprovalActivity.this, R.color.super_blue))
                .setWheelItemTextSize(12);
        if (!TextUtils.isEmpty(type) && type.equals("2")) {
            build.setType(Type.YEAR_MONTH_DAY);
        } else {
            build.setType(Type.ALL);
        }
        pickerDialog = build.build();
        pickerDialog.show(getSupportFragmentManager(), type);
    }

    private void getApproveTemplateDetail() {
        NetBaseRequest request = RequsetFactory.creatBaseRequest(Constants.GET_APPROVE_TEMPLATE_DETAIL);
        request.add("id", id);
        CallServer.getRequestInstance().add(this, 0x01, request, new HttpListenerCallback() {
            @Override
            public void onSucceed(int what, NetBaseBean netBaseBean) {
                if (netBaseBean.isSuccess()) {
                    detail = netBaseBean.parseObject(ApproveTemplate.class);
                    tv_title.setText(detail.getApprove_title());
                    tv_desc.setText(detail.getApprove_desc());
                    adapter.setNewData(detail.getApprove_ext());
                    if (detail.getProcess_type() == 1 && detail.getApprove_user() != null && !TextUtils.isEmpty(detail.getApprove_user().inner)) {
                        //固定流程
                        if (detail.getApprove_user().type == 1) {
                            //人员
                            approve_inner_id = detail.getApprove_user().inner;
                            if (!TextUtils.isEmpty(detail.getApprove_user().nickname)) {
                                tv_user.setText(detail.getApprove_user().nickname);
                            }

                            footerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast("已指定审批人");
                                }
                            });
                        } else if (detail.getApprove_user().type == 2) {
                            if (!TextUtils.isEmpty(detail.getApprove_user().nickname)) {
                                tv_user.setText(detail.getApprove_user().nickname);
                            }
                            //职务
                            footerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ChooseDeptUserActivity.goTo(AddApprovalActivity.this, detail.getApprove_user().inner, TAG);
                                }
                            });
                        }
                    } else {
                        footerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChooseUserActivity.goTo(AddApprovalActivity.this, TAG);
                            }
                        });
                    }
                } else {
                    T.showShort(AddApprovalActivity.this, netBaseBean.getMessage());
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }
        }, true, true);
    }

    private BaseQuickAdapter<ApproveTemplate.Item, BaseViewHolder> adapter = new BaseQuickAdapter<ApproveTemplate.Item, BaseViewHolder>(new ArrayList<ApproveTemplate.Item>()) {
        @Override
        protected void convert(final BaseViewHolder helper, final ApproveTemplate.Item item) {
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
                            RadioButton radioButton = new RadioButton(AddApprovalActivity.this);
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
                            CheckBox checkBox = new CheckBox(AddApprovalActivity.this);
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
                        spinner.setAdapter(new ArrayAdapter<String>(AddApprovalActivity.this, android.R.layout.simple_spinner_item, item.tip_field));
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

    @Subscribe
    public void onEventMainThread(EventRefresh e) {
        if (e.getWhere().equals(TAG) && e.getData() != null) {
            UserInfo userInfo = (UserInfo) e.getData();
            approve_inner_id = userInfo.getId() + "";
            tv_user.setText(userInfo.getNickname());
        }
    }

}
