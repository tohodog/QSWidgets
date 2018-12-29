package org.song.qswidgets.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by song on 2016/9/26.
 * recyclerView适配器基本父类
 */
public abstract class BaseRecyAdapter<M> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int TYPE_HEADER = 346235345;
    private int TYPE_FOOTER = 523432536;

    protected List<M> list;

    protected Context context;

    protected LayoutInflater inflater;

    protected View headerView;

    protected View footerView;


    public List<M> getListData() {
        return list;
    }

    public BaseRecyAdapter(Context context, List<M> list) {
        super();
        if (list == null)
            throw new RuntimeException("List<M> is null");
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    public void update(int item, M itemData) {
        if (itemData == null | item < 0)
            return;
        list.set(item, itemData);
        notifyDataSetChanged();
    }

    public void update(List<M> list) {
        if (list == null)
            this.list.clear();
        else if (this.list != list) {
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addItem(M m) {
        if (m == null)
            return;
        this.list.add(m);
        notifyDataSetChanged();
    }

    public void addItem(int arg0, M item) {
        if (item == null)
            return;
        this.list.add(arg0, item);
        notifyDataSetChanged();
    }

    public void addItem(List<M> list) {
        if (list == null || list.size() == 0)
            return;
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(int arg0, List<M> list) {
        if (list == null || list.size() == 0)
            return;
        this.list.addAll(arg0, list);
        notifyDataSetChanged();
    }


    public void removeItem(int position) {
        if (position >= 0)
            this.list.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(M m) {
        this.list.remove(m);
        notifyDataSetChanged();
    }

    public void clean() {
        this.list.clear();
        notifyDataSetChanged();
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
        //notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
    }


    //gridView 头设置
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            GridLayoutManager.SpanSizeLookup s = gridManager.getSpanSizeLookup();
            if (s != null)//如果用户自己设置了setSpanSizeLookup,跳过
                for (int i = 0; i < 20; i++)
                    if (s.getSpanSize(i) != 1)
                        return;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER ||
                            getItemViewType(position) == TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
    //==========================================================//

    public M getItem(int arg0) {
        if (arg0 >= list.size())
            return null;
        return list.get(arg0);
    }


    public int getItemId(M m) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(m))
                return i;
        return -1;
    }

    @Override
    public int getItemCount() {
        int i = 0;
        if (headerView != null)
            i++;
        if (footerView != null)
            i++;
        return list.size() + i;
    }

    //子类不能覆盖这个方法 覆盖viewType(int );
    @Override
    public final int getItemViewType(int position) {
        if (headerView != null && position == 0)
            return TYPE_HEADER;
        if (footerView != null && position == getItemCount() - 1)
            return TYPE_FOOTER;
        return itemViewType(position);
    }

    public int itemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER)
            return new RecyclerView.ViewHolder(headerView) {
            };
        else if (viewType == TYPE_FOOTER)
            return new RecyclerView.ViewHolder(footerView) {
            };
        else {
            ViewHolder h = bindHolder(parent, inflater, viewType);
            h.setTarget(this);
            return h;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (headerView != null)
            position--;
        if (holder instanceof BaseRecyAdapter.ViewHolder)
            ((ViewHolder) holder).bindData(position, getItem(position));
        else if (holder.getItemViewType() == TYPE_FOOTER)
            if (onFootViewListener != null) onFootViewListener.onFootView();
    }


    public abstract ViewHolder bindHolder(ViewGroup parent, LayoutInflater inflater, int viewType);

    public static abstract class ViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        protected Context context;
        protected LayoutInflater layoutInflater;
        protected BaseRecyAdapter baseRecyAdapter;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
            layoutInflater = LayoutInflater.from(context);
            view.setClickable(true);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public abstract void bindData(int position, T t);

        public void setTarget(BaseRecyAdapter baseRecyAdapter) {
            this.baseRecyAdapter = baseRecyAdapter;
        }

        @Override
        public final void onClick(View v) {
            if (baseRecyAdapter.onItemClickListener != null)
                baseRecyAdapter.onItemClickListener.onItemClick(v,
                        baseRecyAdapter.headerView == null ? getLayoutPosition() : getLayoutPosition() - 1);
        }

        @Override
        public final boolean onLongClick(View v) {
            if (baseRecyAdapter.onItemLongClickListener != null)
                baseRecyAdapter.onItemLongClickListener.onItemLongClick(v,
                        baseRecyAdapter.headerView == null ? getLayoutPosition() : getLayoutPosition() - 1);
            return true;
        }

    }

    //==========================================================//

    private OnItemClickListener onItemClickListener;

    private OnItemLongClickListener onItemLongClickListener;

    private OnFootViewListener onFootViewListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View v, int position);
    }

    public interface OnFootViewListener {
        void onFootView();
    }

}
