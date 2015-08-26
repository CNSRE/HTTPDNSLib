package com.sina.util.dnscache.tasksetting;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.sina.util.dnscache.DNSCacheConfig;
import com.sina.util.dnscache.DNSCacheConfig.Data;
import com.sina.util.dnscache.R;
import com.sina.util.dnscache.simulationtask.TaskManager;
import com.sina.util.dnscache.util.ToastUtil;

/**
 * Created by Doraemon on 2014/7/15.
 */
public class TaskSettingFragment extends Fragment {

    private static final String TAG_COLON = "：";
    private Map<SeekBar, SeekBarInfo> mSeekBarInfoMapping = new HashMap<SeekBar, SeekBarInfo>();
    private EditTextDataProcessor mEditTextDataProcessor = new EditTextDataProcessor();
    private CheckBoxDataProcessor mCheckBoxDataProcessor = new CheckBoxDataProcessor();
    private SeekBarDataProcessor mSeekBarDataProcessor = new SeekBarDataProcessor();
    
    private static Data mEditConfig;
    private static int threadpool_concurrnce_num;
    private static int threadpool_reeust_num;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View contentView = inflater.inflate(R.layout.fragment_task_setting, null);
        ((ScrollView) contentView.findViewById(R.id.scrollView1)).scrollTo(0, 0);
        initSettingInfo(contentView);

        contentView.findViewById(R.id.config_reset).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Data data = Data.createDefault();
                DNSCacheConfig.saveLocalConfigAndSync(getActivity(), data);
                initSettingInfo(contentView);
                
                SpfConfig.getInstance().putInt(R.id.config_threadpool_concurrent_num + "", Config.DEFCONCURRENCYNUM);
                SpfConfig.getInstance().putInt(R.id.config_threadpool_request_num + "", Config.DEFREQUESTSNUM);
                Config.updateThreadpoolConfig();
                TaskManager.getInstance().reInitThreadPool();
                ToastUtil.showText(getActivity(), "恢复成功");
            }
        });
        contentView.findViewById(R.id.config_confirm).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DNSCacheConfig.saveLocalConfigAndSync(getActivity(), mEditConfig);
                
                SpfConfig.getInstance().putInt(R.id.config_threadpool_concurrent_num + "", threadpool_concurrnce_num);
                SpfConfig.getInstance().putInt(R.id.config_threadpool_request_num + "", threadpool_reeust_num);
                Config.updateThreadpoolConfig();
                TaskManager.getInstance().reInitThreadPool();
                
                ToastUtil.showText(getActivity(), "保存成功");
            }
        });

        return contentView;
    }

    private void initSettingInfo(View contentView) {
        //init value
        mEditConfig = DNSCacheConfig.Data.getInstance() ; 
        threadpool_concurrnce_num = Config.concurrencyNum;
        threadpool_reeust_num = Config.requestsNum;
        mSeekBarInfoMapping.clear();
        //find view
        CheckBox checkBox1 = (CheckBox) contentView.findViewById(R.id.checkbox_httpdns);
        CheckBox checkBox2 = (CheckBox) contentView.findViewById(R.id.checkbox_dnspod);
        CheckBox checkBox3 = (CheckBox) contentView.findViewById(R.id.checkbox_sort);

        EditText editText1 = (EditText) contentView.findViewById(R.id.editText_httpdns_api);
        EditText editText2 = (EditText) contentView.findViewById(R.id.editText_dnspod_api);
        EditText editText3 = (EditText) contentView.findViewById(R.id.editText_dnspod_id);
        EditText editText4 = (EditText) contentView.findViewById(R.id.editText_dnspod_key);

        SeekBar seekBar1 = ((SeekBar) contentView.findViewById(R.id.seekbar_threadpool_concurrent_num));
        SeekBar seekBar2 = ((SeekBar) contentView.findViewById(R.id.seekbar_threadpool_request_num));
        SeekBar seekBar3 = ((SeekBar) contentView.findViewById(R.id.seekBar_sort_speed));
        SeekBar seekBar4 = ((SeekBar) contentView.findViewById(R.id.seekBar_sort_priority));
        SeekBar seekBar5 = ((SeekBar) contentView.findViewById(R.id.seekBar_sort_succ_sum));
        SeekBar seekBar6 = ((SeekBar) contentView.findViewById(R.id.seekBar_sort_fail_sum));
        SeekBar seekBar7 = ((SeekBar) contentView.findViewById(R.id.seekBar_sort_recent_succ));

        //setListener
        checkBox1.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox2.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox3.setOnCheckedChangeListener(onCheckedChangeListener);
        
        editText1.addTextChangedListener(new EditTextWatcher(editText1.getId()));
        editText2.addTextChangedListener(new EditTextWatcher(editText2.getId()));
        editText3.addTextChangedListener(new EditTextWatcher(editText3.getId()));
        editText4.addTextChangedListener(new EditTextWatcher(editText4.getId()));
        
        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar4.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar5.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar6.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar7.setOnSeekBarChangeListener(seekBarChangeListener);
        
        //set value
        checkBox1.setChecked(mEditConfig.IS_MY_HTTP_SERVER.equals("1"));
        checkBox2.setChecked(mEditConfig.IS_DNSPOD_SERVER.equals("1"));
        checkBox3.setChecked(mEditConfig.IS_SORT.equals("1"));

        StringBuilder sinaServerApiInfo = new StringBuilder();
        for (int i = 0; i < mEditConfig.HTTPDNS_SERVER_API.size(); i++) {
            String api = mEditConfig.HTTPDNS_SERVER_API.get(i);
            sinaServerApiInfo.append(api);
            sinaServerApiInfo.append("\n");
        }
        if (sinaServerApiInfo.length() > 0) {
            sinaServerApiInfo.deleteCharAt(sinaServerApiInfo.length() - 1);
        }
        editText1.setText(sinaServerApiInfo.toString());
        editText2.setText(mEditConfig.DNSPOD_SERVER_API);
        editText3.setText(mEditConfig.DNSPOD_ID);
        editText4.setText(mEditConfig.DNSPOD_KEY);
       
        mSeekBarInfoMapping.put(seekBar1, new SeekBarInfo((TextView) contentView.findViewById(R.id.config_threadpool_concurrent_num),
                "并发任务" + TAG_COLON + "%d" + "个", Config.concurrencyNum, 10, 1));
        mSeekBarInfoMapping.put(seekBar2, new SeekBarInfo((TextView) contentView.findViewById(R.id.config_threadpool_request_num), "模拟任务总数"
                + TAG_COLON + "%d" + "个", Config.requestsNum, 1000, 10));

        mSeekBarInfoMapping.put(seekBar3, new SeekBarInfo((TextView) contentView.findViewById(R.id.sort_speed),
                "速度插件权重" + TAG_COLON + "%d", Integer.valueOf(mEditConfig.SPEEDTEST_PLUGIN_NUM), 100));
        mSeekBarInfoMapping.put(seekBar4, new SeekBarInfo((TextView) contentView.findViewById(R.id.sort_priority), "服务器推荐权重" + TAG_COLON
                + "%d", Integer.valueOf(mEditConfig.PRIORITY_PLUGIN_NUM), 100));
        mSeekBarInfoMapping.put(seekBar5, new SeekBarInfo((TextView) contentView.findViewById(R.id.sort_succ_sum), "历史成功次数权重" + TAG_COLON
                + "%d", Integer.valueOf(mEditConfig.SUCCESSNUM_PLUGIN_NUM), 100));
        mSeekBarInfoMapping.put(seekBar6, new SeekBarInfo((TextView) contentView.findViewById(R.id.sort_fail_sum), "历史错误次数权重" + TAG_COLON
                + "%d", Integer.valueOf(mEditConfig.ERRNUM_PLUGIN_NUM), 100));
        mSeekBarInfoMapping.put(seekBar7, new SeekBarInfo((TextView) contentView.findViewById(R.id.sort_recent_succ), "最近访问成功权重"
                + TAG_COLON + "%d", Integer.valueOf(mEditConfig.SUCCESSTIME_PLUGIN_NUM), 100));

        Set<SeekBar> keySet = mSeekBarInfoMapping.keySet();
        for (SeekBar seekBar : keySet) {
            SeekBarInfo seekBarInfo = mSeekBarInfoMapping.get(seekBar);
            seekBarInfo.updateView();
            seekBar.setMax(seekBarInfo.getRatioSeekBarMaxProgress());
            seekBar.setProgress(seekBarInfo.getRatioSeekBarProgress());
        }
    }

    static class SeekBarInfo {
        // 展示信息的view
        public TextView lableView;
        // 信息
        public String lableInfo;
        // 当前刻度
        public int scale;
        // 每单位包含的刻度值
        private int unitScale;
        // 最大刻度
        private int maxScale;

        public void updateView() {
            updateView(scale);
        }

        public void updateView(int scale) {
            int lableSscale = scale;
            String msg = String.format(lableInfo, lableSscale);
            lableView.setText(msg);
        }
        
        /**
         * 获取转换前的seekbar的当前刻度
         * @return
         */
        public int getOriginalSeekBarProgress(int ratioProgress) {
            return ratioProgress * unitScale;
        }
        
        /**
         * 获取转换后的seekbar的当前刻度
         * @return
         */
        public int getRatioSeekBarProgress() {
            return scale / unitScale;
        }

        /**
         * 获取转换后的seekbar的最大刻度值
         * @return
         */
        public int getRatioSeekBarMaxProgress() {
            return maxScale / unitScale;
        }
        
        public SeekBarInfo(TextView lableView, String info, int scale, int maxSacle) {
            this(lableView, info, scale, maxSacle, 5);
        }

        public SeekBarInfo(TextView lableView, String info, int scale, int maxSacle, int ratio) {
            this.lableView = lableView;
            this.lableInfo = info;
            this.scale = scale;
            this.maxScale = maxSacle;
            this.unitScale = ratio;
        }
    }

    class EditTextWatcher implements TextWatcher{
        int viewID;
        public EditTextWatcher(int id) {
            this.viewID = id;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mEditTextDataProcessor.update(viewID, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            
        }
        
    }
    OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mCheckBoxDataProcessor.update(buttonView.getId(), isChecked ? "1" : "0");
        }
    };

    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        /**
         * 拖动条停止拖动的时候调用
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        /**
         * 拖动条开始拖动的时候调用
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /**
         * 拖动条进度改变的时候调用
         */
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SeekBarInfo seekBarInfo = mSeekBarInfoMapping.get(seekBar);
            if (0 == progress) {
                seekBar.setProgress(1);
                return;
            }
            int value = seekBarInfo.getOriginalSeekBarProgress(progress);
            seekBarInfo.updateView(value);
            mSeekBarDataProcessor.update(seekBar.getId(), String.valueOf(value));
        }
    };

    static class EditTextDataProcessor implements IViewDataUpdate<String>{

        @Override
        public void update(int id, String value) {
            switch (id) {
            case R.id.editText_httpdns_api:
                String[] array = value.split("\n");
                mEditConfig.HTTPDNS_SERVER_API.clear();
                for (int i = 0; i < array.length; i++) {
                    mEditConfig.HTTPDNS_SERVER_API.add(i, array[i]);
                }
                break;
            case R.id.editText_dnspod_api:
                mEditConfig.DNSPOD_SERVER_API = value;
                break;
            case R.id.editText_dnspod_id:
                mEditConfig.DNSPOD_ID = value;
                break;
            case R.id.editText_dnspod_key:
                mEditConfig.DNSPOD_KEY = value;
                break;
            default:
                break;
            }
        }
        
    }
    static class CheckBoxDataProcessor  implements IViewDataUpdate<String>{

        @Override
        public void update(int id, String value) {
            switch (id) {
            case R.id.checkbox_httpdns:
                mEditConfig.IS_MY_HTTP_SERVER = value;
                break;
            case R.id.checkbox_dnspod:
                mEditConfig.IS_DNSPOD_SERVER = value;
                break;
            case R.id.checkbox_sort:
                mEditConfig.IS_SORT = value;
                break;
            default:
                break;
            }
        }

    }

    static class SeekBarDataProcessor implements IViewDataUpdate<String>{

        public void update(int id, String value) {
            switch (id) {
            case R.id.seekBar_sort_speed:
                mEditConfig.SPEEDTEST_PLUGIN_NUM = value;
                break;
            case R.id.seekBar_sort_priority:
                mEditConfig.PRIORITY_PLUGIN_NUM = value;
                break;
            case R.id.seekBar_sort_succ_sum:
                mEditConfig.SUCCESSNUM_PLUGIN_NUM = value;
                break;
            case R.id.seekBar_sort_fail_sum:
                mEditConfig.ERRNUM_PLUGIN_NUM = value;
                break;
            case R.id.seekBar_sort_recent_succ:
                mEditConfig.SUCCESSTIME_PLUGIN_NUM = value;
                break;
            case R.id.seekbar_threadpool_concurrent_num:
                threadpool_concurrnce_num = Integer.valueOf(value);
                break;
            case R.id.seekbar_threadpool_request_num:
                threadpool_reeust_num = Integer.valueOf(value);
                break;
            default:
                break;
            }
        }
    }

    interface IViewDataUpdate<E> {
        void update(int id, E value);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSeekBarInfoMapping.clear();
    }
}
