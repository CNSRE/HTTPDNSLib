/**
 * 
 */
package com.sina.util.dnscache.cache;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sina.util.dnscache.model.DomainModel;
import com.sina.util.dnscache.model.IpModel;

/**
 *
 * 项目名称: DNSCache <br>
 * 类名称: DNSCacheDatabaseHelper <br>
 * 类描述: 缓存数据库 创建、更新、删除、增删改查相关操作 <br>
 * 创建人: fenglei <br>
 * 创建时间: 2015-3-26 下午4:04:23 <br>
 * 
 * 修改人:  <br>
 * 修改时间:  <br>
 * 修改备注:  <br>
 * 
 * @version V1.0
 */
public class DNSCacheDatabaseHelper extends SQLiteOpenHelper implements DBConstants{

	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * 资源锁
     */
    private final static byte synLock[] = new byte[1];
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 构造函数
     * @param context
     */
    public DNSCacheDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建数据库
     * 
     * 残酷的现实告诉我们，创建多个表时，要分开多次执行db.execSQL方法！！
     */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//Log.d("DB", "onCreate") ;
		db.execSQL(CREATE_DOMAIN_TABLE_SQL);
		db.execSQL(CREATE_IP_TEBLE_SQL);
		db.execSQL(CREATE_CONNECT_FAIL_TABLE_SQL);
	}

	/**
	 * 数据库版本更新策略（直接放弃旧表）
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//Log.d("DB", "onUpgrade") ;
        if (oldVersion != newVersion) {
            // 其它情况，直接放弃旧表.
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DOMAIN + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_IP + ";");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONNECT_FAIL + ";");
            db.setTransactionSuccessful();
            db.endTransaction();
            onCreate(db);
        }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 添加一条新的记录 。如果域名重复删除旧数据。
	 *
	 * @param url
	 * @param sp
	 * @param model
	 * @return
	 */
	public DomainModel addDomainModel( String url, String sp, DomainModel model ){
	    synchronized (synLock) {
	        //过滤重复数据
	        ArrayList<DomainModel> domainList = (ArrayList<DomainModel>) QueryDomainInfo(model.domain, model.sp) ;
	        if( domainList != null && domainList.size() > 0 ) {
	            deleteDomainInfo(domainList) ;
	        }
	        
	        SQLiteDatabase db = getWritableDatabase();
	        try {
	            db.beginTransaction();
	            ContentValues cv = new ContentValues();
	            cv.put(DOMAIN_COLUMN_DOMAIN, model.domain);
	            cv.put(DOMAIN_COLUMN_SP, model.sp);
	            cv.put(DOMAIN_COLUMN_TTL, model.ttl);
	            cv.put(DOMAIN_COLUMN_TIME, model.time);
	            model.id = db.insert(TABLE_NAME_DOMAIN, null, cv);
	            
	            for( int i= 0 ; i < model.ipModelArr.size() ; i++ ){
	                
	                IpModel temp = model.ipModelArr.get(i) ;
	                temp.d_id = model.id;
	                
	                IpModel ipModel = getIpModel(temp.ip, sp) ;
	                
	                
	                if( ipModel == null ){
	                    cv = new ContentValues();
	                    cv.put(IP_COLUMN_DOMAIN_ID, temp.d_id);
//                        cv.put(IP_COLUMN_IP, String.valueOf(Tools.parseIpAddress(temp.ip))  );
	                    cv.put(IP_COLUMN_IP, temp.ip );
	                    cv.put(IP_COLUMN_PORT, temp.port );
	                    cv.put(IP_COLUMN_SP, temp.sp );
	                    cv.put(IP_COLUMN_TTL, temp.ttl );
	                    cv.put(IP_COLUMN_PRIORITY, temp.priority );
	                    cv.put(IP_COLUMN_FINALLY_SPEED, temp.finally_speed );
	                    cv.put(IP_COLUMN_SUCCESS_NUM, temp.success_num );
	                    cv.put(IP_COLUMN_ERR_NUM, temp.err_num );
	                    cv.put(IP_COLUMN_FINALLY_SUCCESS_TIME, temp.finally_success_time );
	                    temp.id = db.insert(TABLE_NAME_IP, null, cv);
	                    //Log.d("TAG","插入数据 temp.id = " + temp.id) ;
	                    
	                }else{
	                    
	                    ipModel.d_id = temp.d_id ; 
	                    ipModel.port = temp.port ;
	                    ipModel.sp = temp.sp ;
	                    ipModel.ttl = temp.ttl ;
	                    ipModel.priority = temp.priority ;
	                    ipModel.finally_speed = Float.parseFloat(temp.finally_speed) == 0 ? ipModel.finally_speed : temp.finally_speed;
	                    ipModel.success_num = String.valueOf( ( ( Integer.parseInt(ipModel.success_num) + Integer.parseInt(temp.success_num) ) ) );
	                    ipModel.err_num = String.valueOf( Integer.parseInt(ipModel.err_num) + Integer.parseInt(temp.err_num) );
	                    ipModel.finally_success_time = temp.finally_success_time ;
	                    setIpModelSpeedInfo( ipModel );
	                    //Log.d("TAG","修改数据 temp.id = " + temp.id) ;
	                    
	                }
	                
	                model.ipModelArr.remove(i) ; 
	                if (null == ipModel) {
                        ipModel = temp;
                    }
	                model.ipModelArr.add(i, ipModel);
	            }
	            
	            db.setTransactionSuccessful();
	        } catch (Exception e) {
	            // TODO: handle exception
	            e.printStackTrace();
	            // 上报错误
	        } finally{
	            db.endTransaction();
	            db.close();
	        }
	        
	        return model ;
        }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 根据url 获取缓存domain
	 * 
	 * @param domain
	 * @param sp
	 * @return
	 */
	public List<DomainModel> QueryDomainInfo(String domain, String sp){

		synchronized (synLock) {
			List<DomainModel> list = new ArrayList<DomainModel>() ;
	        StringBuilder sql = new StringBuilder();
	        sql.append("SELECT * FROM ");
	        sql.append(TABLE_NAME_DOMAIN);
	        sql.append(" WHERE ");
	        sql.append(DOMAIN_COLUMN_DOMAIN);
	        sql.append(" =? ");
	        sql.append(" AND ");
	        sql.append(DOMAIN_COLUMN_SP);
	        sql.append(" =? ;");
	        SQLiteDatabase db = getReadableDatabase();
	        Cursor cursor = null;
			try {
				cursor = db.rawQuery(sql.toString(), new String[] { domain, sp });
				if (cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					do {
						DomainModel model = new DomainModel() ;
						model.id = cursor.getInt(cursor.getColumnIndex(DOMAIN_COLUMN_ID));
						model.domain = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_DOMAIN));
						model.sp = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_SP));
						model.ttl = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_TTL));
                        model.time = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_TIME));
						model.ipModelArr = (ArrayList<IpModel>) QueryIpModelInfo(model) ; 
						list.add(model) ;
	                } while (cursor.moveToNext());
				}
	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				cursor.close();
				db.close() ;
	        }
			return list ; 
		}
	}
	
	/**
	 * 通过url获取服务器ip信息
	 * 根据 domainModel 获取Ipmodel 对象。 
	 * @param domainModel
	 * @return
	 */
	private List<IpModel> QueryIpModelInfo( DomainModel domainModel ){
		
		// 内部方法 不需要加锁
			List<IpModel> list = new ArrayList<IpModel>() ; 
	        StringBuilder sql = new StringBuilder();
	        sql.append("SELECT * FROM ");
	        sql.append(TABLE_NAME_IP);
	        sql.append(" WHERE ");
	        sql.append(IP_COLUMN_DOMAIN_ID);
	        sql.append(" =? ;");
	        SQLiteDatabase db = getReadableDatabase();
	        Cursor cursor = null;
	        try {
	        	cursor = db.rawQuery(sql.toString(), new String[] { String.valueOf( domainModel.id ) });
	        	if (cursor != null && cursor.getCount() > 0) {
	        		cursor.moveToFirst();
	        		do{
	        			IpModel ip = new IpModel() ; 
	        			ip.id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_ID));
	        			ip.d_id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_DOMAIN_ID));
	        			ip.ip = cursor.getString(cursor.getColumnIndex(IP_COLUMN_IP));
//                        ip.ip = String.valueOf( Tools.longToIP( Long.parseLong( ip.ip ) ) );
	        			ip.port = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_PORT));
	        			ip.sp = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SP));
	        			ip.ttl = cursor.getString(cursor.getColumnIndex(IP_COLUMN_TTL));
	        			ip.priority = cursor.getString(cursor.getColumnIndex(IP_COLUMN_PRIORITY));
	        			ip.finally_speed = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SPEED));
	        			ip.success_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SUCCESS_NUM));
	        			ip.err_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_ERR_NUM));
	        			ip.finally_success_time = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SUCCESS_TIME));
	        			list.add(ip) ;
	        		}while(cursor.moveToNext());
	        	}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
				db.close() ;
	        }

	        return list ;
	}
	
	
	/**
	 * 设置测速后的数据
	 * 
	 * @param model
	 * @return
	 */
	public IpModel setIpModelSpeedInfo(IpModel model) {

		synchronized (synLock) {
			SQLiteDatabase db = getReadableDatabase();
	        StringBuilder where = new StringBuilder();
	        where.append(IP_COLUMN_ID);
	        where.append(" = ? ");
			ContentValues cv = new ContentValues();
			cv.put(IP_COLUMN_DOMAIN_ID, model.d_id);
			cv.put(IP_COLUMN_FINALLY_SPEED, model.finally_speed);
			cv.put(IP_COLUMN_SUCCESS_NUM, model.success_num);
			cv.put(IP_COLUMN_ERR_NUM, model.err_num);
			cv.put(IP_COLUMN_FINALLY_SUCCESS_TIME, model.finally_success_time);
			String[] args = new String[]{String.valueOf(model.id)};
	        db.update(TABLE_NAME_IP, cv, where.toString(), args);
			return model;
		}
	}


    public IpModel upDateIpModelSpeedInfo(IpModel model){

    	if( model == null ) return null ;
        IpModel ipModel = getIpModel(model.ip, model.sp);

        // 如果在数据库中找不到对应的 server 则不记录该记录了，因为该记录可能是内置数据， 或者错误数据
        if( ipModel == null ) return null ;

        ipModel.finally_speed = model.finally_speed;
        ipModel.finally_success_time = model.finally_success_time ;
        ipModel.success_num = model.success_num; //String.valueOf( ( Integer.parseInt( ipModel.success_num ) + Integer.parseInt( model.success_num ) ) );
        ipModel.err_num = model.err_num; //String.valueOf( Integer.parseInt( ipModel.err_num ) + Integer.parseInt( model.err_num ) );

        return setIpModelSpeedInfo(ipModel);
    }


    /**
     * 根据 服务器 ip 获取数据库的数据集
     *
     * @param serverIp
     * @return
     */
    private IpModel getIpModel(String serverIp, String sp){

//        serverIp = String.valueOf( Tools.parseIpAddress( serverIp ) ) ;

    	ArrayList<IpModel> list = new ArrayList<IpModel>();

        synchronized (synLock) {

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(TABLE_NAME_IP);
            sql.append(" WHERE ");
            sql.append(IP_COLUMN_IP);
            sql.append(" =? ");
	        sql.append(" AND ");
	        sql.append(DOMAIN_COLUMN_SP);
	        sql.append(" =? ;");
	        
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql.toString(), new String[]{serverIp, sp});
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        IpModel ip = new IpModel();
                        ip.id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_ID));
                        ip.d_id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_DOMAIN_ID));
                        ip.ip = cursor.getString(cursor.getColumnIndex(IP_COLUMN_IP));
                        ip.port = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_PORT));
                        ip.sp = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SP));
                        ip.ttl = cursor.getString(cursor.getColumnIndex(IP_COLUMN_TTL));
                        ip.priority = cursor.getString(cursor.getColumnIndex(IP_COLUMN_PRIORITY));
                        ip.finally_speed = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SPEED));
                        ip.success_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SUCCESS_NUM));
                        ip.err_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_ERR_NUM));
                        ip.finally_success_time = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SUCCESS_TIME));
                        list.add(ip);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
                //db.close();
            }
        }

        // 排除下重复的IP 理论上是不会出现重复IP的， 多线程同时写数据库有锁。
        if( list.size() > 1 ){
        	for( int i = 0 ; i < list.size() - 1 ; i++ ){
        		IpModel ipModel = list.get(i);
        		deleteIpServer(ipModel.id) ; 
        	} 
        }

        return list.size() > 0 ? list.get(list.size() - 1) : null ;
    }
	
	/**
	 * 根据域名id 删除域名相关信息
	 */
	private void deleteDomainInfo(long domain_id){
		
		synchronized (synLock) {
			SQLiteDatabase db = getWritableDatabase();
			try {
				 db.delete(TABLE_NAME_DOMAIN, DOMAIN_COLUMN_ID + " = ?", new String[]{String.valueOf(domain_id)} ) ;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	            db.close();
	        }
		}
	}

//    /**
//     * 根据域名id 删除服务器相关信息
//     * @param domain_id
//     */
//    private void deleteIpInfo(long domain_id){
//
//        synchronized (synLock) {
//            SQLiteDatabase db = getWritableDatabase();
//            try {
//                db.delete(TABLE_NAME_IP, IP_COLUMN_DOMAIN_ID + " = ?", new String[]{String.valueOf(domain_id)} ) ;
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                db.close();
//            }
//        }
//    }
    
    /**
     * 根据 ID 删除服务器信息
     * @param ip
     */
    private void deleteIpServer(long id){

        synchronized (synLock) {
            SQLiteDatabase db = getWritableDatabase();
            try {
                db.delete(TABLE_NAME_IP, IP_COLUMN_ID + " = ?", new String[]{String.valueOf(id)} ) ;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.close();
            }
        }
    }


	/**
	 * 删除域名相关信息
	 */
	public void deleteDomainInfo(DomainModel domainModel){
		deleteDomainInfo( domainModel.id) ;
	}
	
	/**
	 * 删除域名相关信息
	 */
	public void deleteDomainInfo(ArrayList<DomainModel> domainModelArr){
		for( DomainModel temp : domainModelArr )
			deleteDomainInfo( temp.id) ;
	}
	
	
    /**
     * 清除缓存数据
     */
	public void clear() {
	    synchronized (synLock) {
	        SQLiteDatabase db = getWritableDatabase();
	        try {
	            db.delete(TABLE_NAME_DOMAIN, null, null);
	            db.delete(TABLE_NAME_IP, null, null);
	            db.delete(TABLE_NAME_CONNECT_FAIL, null, null);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            db.close();
	        }
        }
	}


    /**
     * 返回 domain  表信息
     */
    public ArrayList<DomainModel> getAllTableDomain() {
        ArrayList<DomainModel> list = new ArrayList<DomainModel>();
        synchronized (synLock) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(TABLE_NAME_DOMAIN);
            sql.append(" ; ");
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql.toString(), null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        DomainModel model = new DomainModel();
                        model.id = cursor.getInt(cursor.getColumnIndex(DOMAIN_COLUMN_ID));
                        model.domain = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_DOMAIN));
                        model.sp = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_SP));
                        model.ttl = cursor.getString(cursor.getColumnIndex(DOMAIN_COLUMN_TTL));
                        // model.ipModelArr = (ArrayList<IpModel>)
                        // QueryIpModelInfo(model) ;
                        list.add(model);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                cursor.close();
                db.close();
            }
        }
        return list;
    }


    /**
     * 返回 ip 表信息
     */
    public ArrayList<IpModel> getTableIP(){
        synchronized (synLock) {
        	ArrayList<IpModel> list = new ArrayList<IpModel>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(TABLE_NAME_IP);
            sql.append(" ; ");

            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql.toString(), null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        IpModel ip = new IpModel();
                        ip.id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_ID));
                        ip.d_id = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_DOMAIN_ID));
                        ip.ip = cursor.getString(cursor.getColumnIndex(IP_COLUMN_IP));
                        ip.port = cursor.getInt(cursor.getColumnIndex(IP_COLUMN_PORT));
                        ip.sp = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SP));
                        ip.ttl = cursor.getString(cursor.getColumnIndex(IP_COLUMN_TTL));
                        ip.priority = cursor.getString(cursor.getColumnIndex(IP_COLUMN_PRIORITY));
                        ip.finally_speed = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SPEED));
                        ip.success_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_SUCCESS_NUM));
                        ip.err_num = cursor.getString(cursor.getColumnIndex(IP_COLUMN_ERR_NUM));
                        ip.finally_success_time = cursor.getString(cursor.getColumnIndex(IP_COLUMN_FINALLY_SUCCESS_TIME));
                        list.add(ip);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
                db.close();
            }
            return list ; 
        }


    }




	
}
