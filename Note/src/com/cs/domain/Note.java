package com.cs.domain;

import cn.bmob.v3.BmobObject;

public class Note extends BmobObject{
	// 存放在数据库中的属性
	private String _id;	//主键
	
    private String title;
    private String content;
    private String createTime;

    private String userName;		//外键
    private boolean isSync = false; // 标志该Note是否已经同步
    
   
	public Note(String title, String content, String createTime) {
        this.title = title;
        this.content = content;
        this.createTime = createTime;
    }

    public Note() {
    }

    
    public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    

    public boolean isSync() {
		return isSync;
	}

	public void setSync(boolean isSync) {
		this.isSync = isSync;
	}

	@Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Note note = (Note) o;

        if (!title.equals(note.title)) return false;
        if (!content.equals(note.content)) return false;
        return createTime.equals(note.createTime);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + createTime.hashCode();
        return result;
    }

    
}
