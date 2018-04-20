select
    (regexp_split_to_array(title_simple, '(?<=]);'))[1] title_simple,
    id_,
    version,
    folder,
    createdate,
    modifieddate,
    username modified_by,
    length(content) size_,
    title_simple title_full,
    (
        select string_agg((select name from journalfolder jf where jf.folderid = t.folderid::integer), ' > ')
        from (select regexp_split_to_table(treepath, '/') folderid) t
        where t.folderid != ''
    ) full_path from (SELECT
  uuid_,
  id_,
  resourceprimkey,
  groupid,
  companyid,
  userid,
  username,
  createdate,
  modifieddate,
  folderid,
  (select name from journalfolder jf where jf.folderid = ja.folderid) folder,
  classnameid,
  classpk,
  treepath,
  articleid,
  version,
  max(version) over (partition by articleid) max_version,
  title,
  (xpath('./Title/text()', title::xml))[1]::text title_simple,
  description,
  content,
  type_,
  structureid,
  templateid,
  layoutuuid,
  displaydate,
  expirationdate,
  reviewdate,
  indexable,
  smallimage,
  smallimageid,
  smallimageurl,
  status,
  statusbyuserid,
  statusbyusername,
  statusdate
FROM journalarticle ja
WHERE groupid = '20306' /*Cloud site*/) as t
WHERE t.version = t.max_version
ORDER BY t.treepath, t.title_simple;