package com.my.filemanager;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ListActivity implements AdapterView.OnItemLongClickListener {
    //声明成员变量
    //存放显示的文件列表的名称
    private List<String> mFileName = null;
    //存放显示的文件列表的相对应的路径
    private List<String> mFilePaths = null;
    //起始目录"/"
    private String mRootPath = File.separator;
    //SD卡根目录
    private String mSDCard = Environment.getExternalStorageState().toString();
    private String mOldFilePath = "";
    private String mNewFilePath = "";
    private String keyWords;
    //用于显示当前路径
    private TextView mPath;
    //用于存放工具栏
    private GridView mGridViewToolbar;
    private int[] gridview_menu_image = {R.drawable.phone, R.drawable.sd, R.drawable.search, R.drawable.create, R.drawable.paste, R.drawable.exit};
    private String[] gridview_menu_title = {"手机", "SD卡", "搜索", "创建", "粘贴", "退出"};
    //代表手机或SD卡,1代表手机，2代表SD卡
    private static int menuPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGridViewMenu();
        initMenuListener();

        getListView().setOnItemLongClickListener(this);
        mPath = findViewById(R.id.mPath);

        initFileListInfo(mRootPath);
    }

    /*public class FileService extends Service {
        private Looper mLooper;
        private FileHandler mFileHandler;
        private ArrayList<String> mFileName=null;
        private ArrayList<String> mFilePaths=null;
        public static final String FILE_SEARCH_COMPLETED="com.my.filemanager.FILE_NOTIFICATION";
        public static final String FILE_NOTIFICATION="com.my.filemanager.FILE_NOTIFICATION";
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            HandlerThread mHT=new HandlerThread("FileService",HandlerThread.NORM_PRIORITY);
            mHT.start();;
            mLooper=mHT.getLooper();
            mFileHandler=new FileHandler(mLooper);
        }

        @Override
        public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);
            mFileName=new ArrayList<>();
            mFilePaths=new ArrayList<>();
            mFileHandler.sendEmptyMessage(0);
            fileSearchNotification();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mNF.cancel(R.string.app_name);
        }

        class FileHandler extends Handler{
            public FileHandler(Looper looper){
                super(looper);
            }

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                initFileArray(new File(SearchBroadCast.mServiceSearchPath));
                if(!MainActivity.)
            }
        }
    }*/

    private void initFileListInfo(String filePath) {
        isAddBackUp = false;
        mCurrentFilePath = filePath;

        mPath.setText(filePath);
        mFileName = new ArrayList<String>();
        mFilePaths = new ArrayList<String>();
        File mFile = new File(filePath);
        File[] mFiles = mFile.listFiles();

        if (menuPosition == 1 && mCurrentFilePath.equals(mRootPath)) {
            initAddBackUp(filePath, mRootPath);
        } else if (menuPosition == 2 && mCurrentFilePath.equals(mSDCard)) {
            initAddBackUp(filePath, mSDCard);
        }

        for (File mCurrentFile : mFiles) {
            mFileName.add(mCurrentFile.getName());
            mFilePaths.add(mCurrentFile.getPath());
        }

        setListAdapter(new FileAdapter(MainActivity.this, mFileName, mFilePaths));
    }

    class FileAdapter extends BaseAdapter {
        private Bitmap mBackRoot;
        private Bitmap mBackUp;
        private Bitmap mImage;
        private Bitmap mAudio;
        private Bitmap mRar;
        private Bitmap mVideo;
        private Bitmap mFolder;
        private Bitmap mApk;
        private Bitmap mOthers;
        private Bitmap mTxt;
        private Bitmap mWeb;

        private Context mContent;
        private List<String> mFileNameList;
        private List<String> mFilePathList;

        public FileAdapter(Context context, List<String> fileName, List<String> filePath) {
            mContent = context;
            mFileNameList = fileName;
            mFilePathList = filePath;

            mBackRoot = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.root);
            mBackUp = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.back_up);
            mImage = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.mimage);
            mAudio = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.audio);
            mRar = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.zip);
            mVideo = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.vidio);
            mFolder = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.folder);
            mApk = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.apk);
            mOthers = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.others);
            mTxt = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.txt);
            mWeb = BitmapFactory.decodeResource(mContent.getResources(), R.drawable.web);
        }

        public int getCount() {
            return mFileNameList.size();
        }

        public Object getItem(int position) {
            return mFilePathList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View contentView, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (contentView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater mLI = (LayoutInflater) mContent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                contentView = mLI.inflate(R.layout.list_child, null);
                viewHolder.mIV = contentView.findViewById(R.id.image_list_childs);
                viewHolder.mTV = contentView.findViewById(R.id.text_list_childs);
                contentView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            File mFile = new File(mFileNameList.get(position).toString());
            if (mFileNameList.get(position).toString().equals("BacktoRoot")) {
                viewHolder.mIV.setImageBitmap(mBackRoot);
                viewHolder.mTV.setText("返回根目录");
            } else if (mFileNameList.get(position).toString().equals("BacktoUp")) {
                viewHolder.mIV.setImageBitmap(mBackUp);
                viewHolder.mTV.setText("返回上一级");
            } else if (mFileNameList.get(position).toString().equals("BacktoSearchBefore")) {
                viewHolder.mIV.setImageBitmap(mBackRoot);
                viewHolder.mTV.setText("返回搜索前目录");
            } else {
                String fileName = mFile.getName();
                viewHolder.mTV.setText(fileName);
                if (mFile.isDirectory()) {
                    viewHolder.mIV.setImageBitmap(mFolder);
                } else {
                    String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
                    if (fileEnds.equals("m4a") || fileEnds.equals("mp3") || fileEnds.equals("mid") || fileEnds.equals("xmf") || fileEnds.equals("ogg") || fileEnds.equals("wav")) {
                        viewHolder.mIV.setImageBitmap(mVideo);
                    } else if (fileEnds.equals("3gp") || fileEnds.equals("mp4")) {
                        viewHolder.mIV.setImageBitmap(mAudio);
                    } else if (fileEnds.equals("jpg") || fileEnds.equals("gif") || fileEnds.equals("png") || fileEnds.equals("jpeg") || fileEnds.equals("bmp")) {
                        viewHolder.mIV.setImageBitmap(mImage);
                    } else if (fileEnds.equals("apk")) {
                        viewHolder.mIV.setImageBitmap(mApk);
                    } else if (fileEnds.equals("txt")) {
                        viewHolder.mIV.setImageBitmap(mTxt);
                    } else if (fileEnds.equals("zip") || fileEnds.equals("rar")) {
                        viewHolder.mIV.setImageBitmap(mRar);
                    } else if (fileEnds.equals("html") || fileEnds.equals("htm") || fileEnds.equals("mht")) {
                        viewHolder.mIV.setImageBitmap(mWeb);
                    } else {
                        viewHolder.mIV.setImageBitmap(mOthers);
                    }
                }
            }
            return contentView;
        }

        class ViewHolder {
            ImageView mIV;
            TextView mTV;
        }
    }

    private void initAddBackUp(String filePath, String phone_sdcard) {
        if (!filePath.equals(phone_sdcard)) {
            mFileName.add("BacktoRoot");
            mFilePaths.add(phone_sdcard);

            mFileName.add("BacktoUp");
            mFilePaths.add(new File(filePath).getParent());
            isAddBackUp = true;
        }
    }

    private void initMenuListener() {
        mGridViewToolbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2) {
                    case 0:
                        menuPosition = 1;
                        initFileListInfo(mRootPath);
                        break;
                    case 1:
                        menuPosition = 2;
                        initFileListInfo(mSDCard);
                        break;
                    case 2:
                        searchDialog();
                        break;
                    case 3:
                        createFolder();
                        break;
                    case 4:
                        pasteFile();
                        break;
                    case 5:
                        MainActivity.this.fileList();
                        break;
                }
            }
        });
    }

    private void pasteFile() {
        mNewFilePath = mCurrentFilePath + File.separator + mCopyFileName;
        if (!mOldFilePath.equals(mNewFilePath) && isCopy == true) {
            if (!new File(mNewFilePath).exists()) {
                Toast.makeText(this, "执行了粘贴", Toast.LENGTH_SHORT).show();
                initFileListInfo(mCurrentFilePath);
            } else {
                new AlertDialog.Builder(MainActivity.this).setTitle("提示！").setMessage("该文件已存在，是否要覆盖？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyFile(mOldFilePath, mNewFilePath);
                        initFileListInfo(mCurrentFilePath);
                    }
                }).setNegativeButton("取消", null).show();
            }
        } else {
            Toast.makeText(this, "未复制文件", Toast.LENGTH_SHORT).show();
        }
    }

    private int i;
    FileInputStream fis;
    FileOutputStream fos;

    private void copyFile(String oldFile, String newFile) {
        try {
            fis = new FileInputStream(oldFile);
            fos = new FileOutputStream(newFile);
            do {
                if ((i = fis.read()) != -1) {
                    fos.write(i);
                }
            } while (i != -1);
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String mCurrentFilePath = "";
    private String mNewFolderName = "";
    private File mCreateFile;
    private RadioGroup mCreateRadioGroup;
    private static int mChecked;

    private void createFolder() {
        mChecked = 2;
        LayoutInflater mLI = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.create_dialog, null);
        mCreateRadioGroup = mLL.findViewById(R.id.radiogroup_create);
        final RadioButton mCreateFileButton = findViewById(R.id.create_file);
        final RadioButton mCreateFolderButton = findViewById(R.id.create_folder);
        mCreateFolderButton.setChecked(true);
        mCreateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mCreateFileButton.getId()) {
                    mChecked = 1;
                } else if (checkedId == mCreateFolderButton.getId()) {
                    mChecked = 2;
                }
            }
        });

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this).setTitle("新建").setView(mLL).setPositiveButton("创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNewFolderName = ((EditText) mLL.findViewById(R.id.new_filename)).getText().toString();
                if (mChecked == 1) {
                    try {
                        mCreateFile = new File(mCurrentFilePath + File.separator + mNewFolderName + ".txt");
                        mCreateFile.createNewFile();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "文件名拼接出错。。", Toast.LENGTH_SHORT).show();
                    }
                } else if (mChecked == 2) {
                    mCreateFile = new File(mCurrentFilePath + File.separator + mNewFolderName);
                    if (!mCreateFile.exists() && !mCreateFile.isDirectory() && mNewFolderName.length() != 0) {
                        if (mCreateFile.mkdirs()) {
                            initFileListInfo(mCurrentFilePath);
                        } else {
                            Toast.makeText(MainActivity.this, "创建失败，可能是系统权限不够，root一下？", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "文件名为空，还是重名了呢？", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).setNeutralButton("取消", null);
        mBuilder.show();
    }

    EditText mET;

    private void initRenameDialog(final File file) {
        LayoutInflater mLI = LayoutInflater.from(MainActivity.this);
        LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.rename_dialog, null);
        mET = mLL.findViewById(R.id.new_filename);

        mET.setText(file.getName());

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String modifyName = mET.getText().toString();
                final String modifyFilePath = file.getParentFile().getPath() + File.separator;
                final String newFilePath = modifyFilePath + modifyName;
                if (new File(newFilePath).exists()) {
                    if (!modifyName.equals(file.getName())) {
                        new AlertDialog.Builder(MainActivity.this).setTitle("提示！").setMessage("该文件名已存在，是否要覆盖？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "the file path is " + new File(newFilePath), Toast.LENGTH_SHORT).show();
                                initFileListInfo(file.getParentFile().getPath());
                            }
                        }).setNegativeButton("取消", null).show();
                    }
                } else {
                    file.renameTo(new File(newFilePath));
                    initFileListInfo(file.getParentFile().getPath());
                }
            }
        };
        AlertDialog renameDialog = new AlertDialog.Builder(MainActivity.this).create();
        renameDialog.setView(mLL);
        renameDialog.setButton("确定", listener);
        renameDialog.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        renameDialog.show();
    }

    private void initDeleteDialog(final File file) {
        new AlertDialog.Builder(MainActivity.this).setTitle("提示！").setMessage("您确定删除该" + (file.isDirectory() ? "文件夹" : "文件") + "吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    deleteFolder(file);
                }
                initFileListInfo(file.getParent());
            }
        }).setNegativeButton("取消", null).show();
    }

    public void deleteFolder(File folder) {
        File[] fileArray = folder.listFiles();
        if (fileArray.length == 0) {
            folder.delete();
        } else {
            for (File currentFile : fileArray) {
                if (currentFile.exists() && currentFile.isFile()) {
                    currentFile.delete();
                } else {
                    deleteFolder(currentFile);
                }
            }
        }
    }

    private void searchDialog() {
    }

    private void initGridViewMenu() {
        mGridViewToolbar = findViewById(R.id.file_gridview_toolbar);
        mGridViewToolbar.setNumColumns(6);
        mGridViewToolbar.setGravity(Gravity.CENTER);
        mGridViewToolbar.setVerticalSpacing(10);
        mGridViewToolbar.setHorizontalSpacing(10);
        mGridViewToolbar.setAdapter(getMenuAdapter(gridview_menu_title, gridview_menu_image));
    }

    private SimpleAdapter getMenuAdapter(String[] menuNameArray, int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> mData = new ArrayList<>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> mMap = new HashMap<>();
            mMap.put("image", imageResourceArray[i]);
            mMap.put("title", menuNameArray[i]);
            mData.add(mMap);
        }
        SimpleAdapter mAdapter = new SimpleAdapter(this, mData, R.layout.item_menu, new String[]{"image", "title"}, new int[]{R.id.item_image, R.id.item_text});
        return mAdapter;
    }

    private boolean isAddBackUp = false;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (isAddBackUp == true) {
            if (position != 0 && position != 1) {
                initItemLongClickListener(new File(mFilePaths.get(position)));
            }
        }
        if (mCurrentFilePath.equals(mRootPath) || mCurrentFilePath.equals(mSDCard)) {
            initItemLongClickListener(new File(mFilePaths.get(position)));
        }
        return false;
    }

    private String mCopyFileName;
    private boolean isCopy = false;

    private void initItemLongClickListener(final File file) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (file.canRead()) {
                    if (item == 0) {
                        if (file.isFile() && "txt".equals((file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length())).toLowerCase())) {
                            ;
                            Toast.makeText(MainActivity.this, "已复制", Toast.LENGTH_SHORT).show();

                            isCopy = true;
                            mCopyFileName = file.getName();
                            mOldFilePath = mCurrentFilePath + File.separator + mCopyFileName;
                        } else {
                            Toast.makeText(MainActivity.this, "对不起目前只支持复制文本文件！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (item == 1) {
                        initRenameDialog(file);
                    } else if (item == 2) {
                        initDeleteDialog(file);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "对不起，您的访问权限不足！", Toast.LENGTH_SHORT).show();
                }
            }
        };
        String[] mMenu = {"复制", "重命名", "删除"};
        new AlertDialog.Builder(MainActivity.this).setTitle("请选择操作").setItems(mMenu, listener).setPositiveButton("取消", null).show();
    }
}
