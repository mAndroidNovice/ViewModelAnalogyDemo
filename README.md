# ViewModelAnalogyDemo
&emsp;&emsp;这个项目就是一个ViewModel的使用demo,只有rxjava2,viewmodel,uI viewmodel  data做了分层,简单易懂,也可以明白android room是如果动态insert后通知数据修改的
代码没有什么复杂的 看一下就懂了

&emsp;&emsp;值得一提的是 factory在create viewmodel的时候 会从缓存中读取,相应代码如下
FragmentActivity.java
```
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (getApplication() == null) {
            throw new IllegalStateException("Your activity is not yet attached to the "
                    + "Application instance. You can't request ViewModel before onCreate call.");
        }
        if (mViewModelStore == null) {
            NonConfigurationInstances nc =
                    (NonConfigurationInstances) getLastNonConfigurationInstance();
            if (nc != null) {
                // Restore the ViewModelStore from NonConfigurationInstances
                mViewModelStore = nc.viewModelStore;
            }
            if (mViewModelStore == null) {
                mViewModelStore = new ViewModelStore();
            }
        }
        return mViewModelStore;
    }
```
&emsp;&emsp;在当前页面销毁时清除
```
/**
     * Destroy all fragments.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mViewModelStore != null && !isChangingConfigurations()) {
            mViewModelStore.clear();
        }

        mFragments.dispatchDestroy();
    }
```
&emsp;&emsp; 屏幕旋转时通过 onRetainNoConfigurationInstance保留
```
 /**
     * Retain all appropriate fragment state.  You can NOT
     * override this yourself!  Use {@link #onRetainCustomNonConfigurationInstance()}
     * if you want to retain your own state.
     */
    @Override
    public final Object onRetainNonConfigurationInstance() {
        Object custom = onRetainCustomNonConfigurationInstance();

        FragmentManagerNonConfig fragments = mFragments.retainNestedNonConfig();

        if (fragments == null && mViewModelStore == null && custom == null) {
            return null;
        }

        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.custom = custom;
        nci.viewModelStore = mViewModelStore;
        nci.fragments = fragments;
        return nci;
    }
```

&emsp;&emsp; 在onCreate的时候恢复
```
/**
     * Perform initialization of all fragments.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mFragments.attachHost(null /*parent*/);

        super.onCreate(savedInstanceState);

        NonConfigurationInstances nc =
                (NonConfigurationInstances) getLastNonConfigurationInstance();
        if (nc != null && nc.viewModelStore != null && mViewModelStore == null) {
            mViewModelStore = nc.viewModelStore;
        }
        if (savedInstanceState != null) {
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            mFragments.restoreAllState(p, nc != null ? nc.fragments : null);

            // Check if there are any pending onActivityResult calls to descendent Fragments.
            if (savedInstanceState.containsKey(NEXT_CANDIDATE_REQUEST_INDEX_TAG)) {
                mNextCandidateRequestIndex =
                        savedInstanceState.getInt(NEXT_CANDIDATE_REQUEST_INDEX_TAG);
                int[] requestCodes = savedInstanceState.getIntArray(ALLOCATED_REQUEST_INDICIES_TAG);
                String[] fragmentWhos = savedInstanceState.getStringArray(REQUEST_FRAGMENT_WHO_TAG);
                if (requestCodes == null || fragmentWhos == null ||
                            requestCodes.length != fragmentWhos.length) {
                    Log.w(TAG, "Invalid requestCode mapping in savedInstanceState.");
                } else {
                    mPendingFragmentActivityResults = new SparseArrayCompat<>(requestCodes.length);
                    for (int i = 0; i < requestCodes.length; i++) {
                        mPendingFragmentActivityResults.put(requestCodes[i], fragmentWhos[i]);
                    }
                }
            }
        }

        if (mPendingFragmentActivityResults == null) {
            mPendingFragmentActivityResults = new SparseArrayCompat<>();
            mNextCandidateRequestIndex = 0;
        }

        mFragments.dispatchCreate();
    }
```

    
