select 
  title,
  fileentryid,
  version,
  folder,
  createdate,
  username created_by,
  modifieddate,
  (select username from dlfileversion dlfv where dlfv.version = t.version and dlfv.fileentryid = t.fileentryid) modified_by,
  extension,
  size_,
  treepath,
  mimetype
from (
  SELECT 
    uuid_,
    fileentryid,
    groupid,
    companyid,
    userid,
    username,
    createdate,
    modifieddate,
    classnameid,
    classpk,
    repositoryid,
    folderid,
    (select name from dlfolder dlf where dlf.folderid = dle.folderid) folder,
    treepath,
    name,
    extension,
    mimetype,
    title,
    description,
    extrasettings,
    fileentrytypeid,
    version,
    size_,
    readcount,
    smallimageid,
    largeimageid,
    custom1imageid,
    custom2imageid,
    manualcheckinrequired
  FROM dlfileentry dle
  WHERE groupid = 20306/*Cloud*/ and repositoryid = 20306/*Cloud*/) t
ORDER BY folder, title;

