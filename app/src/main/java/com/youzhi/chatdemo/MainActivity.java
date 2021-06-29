package com.youzhi.chatdemo;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.youzhi.chatdemo.chat.FriendListFragment;
import com.youzhi.chatdemo.constant.AppConst;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.rg_bottom)
    RadioGroup rgBottom;
    private List<BaseFragment> baseFragments;
    private int initSelectedPosition = 0;
    int tabSelected = 0;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initFragment();
        rgBottom.setOnCheckedChangeListener(this);
    }

    private void initFragment() {
        if (baseFragments == null) {
            baseFragments = new ArrayList<>();
            FriendListFragment friendListFragment = new FriendListFragment();
            baseFragments.add(friendListFragment);
        }
        if (getSavedInstanceState() == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fl_main, baseFragments.get(initSelectedPosition)).commitAllowingStateLoss();
        } else {
            //获取到保存的数据状态进行对应角标额fragment切换
            Bundle savedInstanceState = getSavedInstanceState();
            switchFragment(savedInstanceState.getInt(AppConst.KEY_FRAGMENT_STATUS_SAVE));
        }

    }

    /**
     * 防止app长期处在后台被杀死，保存一些数据状态目前保存的事切换时对应的角标
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(AppConst.KEY_FRAGMENT_STATUS_SAVE, tabSelected);
        super.onSaveInstanceState(outState);

    }

    /*切换Fragment*/
    private void switchFragment(int position) {
        tabSelected = position;
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> list = fragmentManager.getFragments();
        BaseFragment targetFragment = baseFragments.get(position);

        if (!list.contains(targetFragment)) {
            fragmentManager.beginTransaction().add(R.id.fl_main, targetFragment).commitAllowingStateLoss();
        }
        for (Fragment fragment : list) {
            if (fragment == targetFragment) continue;
            fragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
        }
        fragmentManager.beginTransaction().show(targetFragment).commitAllowingStateLoss();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switchFragment(group.indexOfChild(group.findViewById(checkedId)));

    }
}