-- update default organization id
update org_organization t set t.id = 'O0000000001' where t.type_code = '000101';
