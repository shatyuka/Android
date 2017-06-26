package me.ghui.v2er.module.create;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import me.ghui.v2er.R;
import me.ghui.v2er.general.PreConditions;
import me.ghui.v2er.injector.component.DaggerCreateTopicComponnet;
import me.ghui.v2er.injector.module.CreateTopicModule;
import me.ghui.v2er.module.base.BaseActivity;
import me.ghui.v2er.module.topic.TopicActivity;
import me.ghui.v2er.network.bean.CreateTopicPageInfo;
import me.ghui.v2er.network.bean.TopicInfo;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.widget.dialog.BaseDialog;
import me.ghui.v2er.widget.dialog.ConfirmDialog;

/**
 * Created by ghui on 04/06/2017.
 */

public class CreateTopicActivity extends BaseActivity<CreateTopicContract.IPresenter> implements CreateTopicContract.IView,
        Toolbar.OnMenuItemClickListener, NodeSelectFragment.OnSelectedListener {

    @BindView(R.id.create_topic_title_layout)
    TextInputLayout mTitleTextInputLayout;
    @BindView(R.id.create_topic_title_et)
    EditText mTitleEt;
    @BindView(R.id.create_topic_content_et)
    EditText mContentEt;
    @BindView(R.id.create_topic_node_wrapper)
    View mNodeWrappter;
    @BindView(R.id.create_topic_node_tv)
    TextView mNodeTv;
    private CreateTopicPageInfo mTopicPageInfo;
    private CreateTopicPageInfo.BaseNode mSelectNode;

    @Override
    protected int attachLayoutRes() {
        return R.layout.act_new_topic;
    }

    @Override
    protected void startInject() {
        DaggerCreateTopicComponnet.builder().appComponent(getAppComponent())
                .createTopicModule(new CreateTopicModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void autoLoad() {
        mPresenter.start();
    }

    @Override
    protected void configToolBar(Toolbar toolBar) {
        super.configToolBar(toolBar);
        toolBar.inflateMenu(R.menu.post_topic_menu);//设置右上角的填充菜单
        toolBar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_create_topic:
                String title = mTitleEt.getText().toString();
                if (PreConditions.isEmpty(title)) {
                    mTitleTextInputLayout.setError("请输入标题");
                    return false;
                }
                if (mSelectNode == null || PreConditions.isEmpty(mSelectNode.getId())) {
                    Toast.makeText(this, "请选择一个节点", Toast.LENGTH_SHORT).show();
                    return false;
                }
                String content = mContentEt.getText().toString();
                mPresenter.sendPost(title, content, mSelectNode.getId());
                return true;
        }
        return false;
    }

    @OnClick(R.id.create_topic_node_wrapper)
    void onNodeWrapperClicked(View view) {
        if (mTopicPageInfo == null) {
            return;
        }
        view.setClickable(false);
        NodeSelectFragment.newInstance(mTopicPageInfo).show(getFragmentManager(), null);
        view.setClickable(true);
    }

    @Override
    public void fillView(CreateTopicPageInfo topicPageInfo) {
        mTopicPageInfo = topicPageInfo;
    }


    @Override
    public void onPostSuccess(TopicInfo topicInfo) {
        toast("创建成功");
        TopicActivity.open(topicInfo.getTopicLink(), this);
        finish();
    }

    @Override
    public void onPostFailure(CreateTopicPageInfo createTopicPageInfo) {
        fillView(createTopicPageInfo);
        CreateTopicPageInfo.Problem problem = createTopicPageInfo.getProblem();
        String msg = null;
        for (String tip : problem.getTips()) {
            msg = msg + tip + "\n";
        }
        new ConfirmDialog.Builder(getActivity())
                .title(problem.getTitle())
                .msg(msg)
                .positiveText(R.string.ok, null)
                .build().show();
    }

    @Override
    public void onSelected(CreateTopicPageInfo.BaseNode node) {
        mSelectNode = node;
        mNodeTv.setText(node.getTitle());
    }

    @Override
    public void onBackPressed() {
        if (mTitleEt.getText().length() > 0 || mContentEt.getText().length() > 0) {
            new ConfirmDialog.Builder(this)
                    .title("丢弃主题")
                    .msg("返回将丢弃当前编写的内容")
                    .positiveText(R.string.ok, dialog -> CreateTopicActivity.super.onBackPressed())
                    .negativeText(R.string.cancel)
                    .build().show();
        } else {
            super.onBackPressed();
        }
    }
}
