<template>
  <div id="catalogEdit" v-loading="isLoging">
    <el-dialog title="节点编辑" width="40%" top="2rem" :append-to-body="true" :close-on-click-modal="false"
      :visible.sync="showDialog" :destroy-on-close="true" @close="close()">
      <div id="shared" style="margin-top: 1rem;margin-right: 100px;">
        <!--  -->
        <el-form v-if="otype == 1 || otype == 2" ref="form" :rules="rules" :model="form" label-width="140px">
          <!--          <el-form-item >-->
          <!--            建议的类型：-->
          <!--            <br/>-->
          <!--            &emsp;&emsp;行政区划（可选2位/4位/6位/8位/10位数字，例如：130432，表示河北省邯郸市广平县）-->
          <!--            <br/>-->
          <!--            &emsp;&emsp;业务分组（第11、12、13位215，例如：34020000002150000001）-->
          <!--            <br/>-->
          <!--            &emsp;&emsp;虚拟组织（第11、12、13位216，例如：34020000002160000001）-->
          <!--          </el-form-item>-->
          <el-form-item label="节点编号" prop="catalogId">
            <el-input v-model="form.catalogId" :disabled="otype == 2" clearable></el-input>
          </el-form-item>
          <el-form-item label="节点名称" prop="name">
            <el-input v-model="form.name" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <div style="float: right;">
              <el-button type="primary" @click="onSubmit">确认</el-button>
              <el-button @click="close">取消</el-button>
            </div>

          </el-form-item>
        </el-form>
        <!--  -->
        <el-form v-if="otype == 3" ref="dform" :rules="drules" :model="dform" label-width="140px">
          <el-form-item label="父节点">
            <el-input v-model="dform.parentName" disabled></el-input>
          </el-form-item>
          <el-form-item label="设备编号" prop="deviceId">
            <el-input v-model="dform.deviceId" disabled></el-input>
          </el-form-item>
          <el-form-item label="导入设备" prop="catalogId">
            <el-select v-model="dform.deviceName" placeholder="请选择" @change="changeDevice" style="width: 100%">
              <el-option v-for="item in deviceList" :key="item.deviceId" :label="item.name" :value="item.deviceId">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="节点编号" prop="catalogId">
            <el-input v-model="dform.catalogId" clearable></el-input>
          </el-form-item>
          <el-form-item label="节点名称" prop="name">
            <el-input v-model="dform.name" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <div style="float: right;">
              <el-button type="primary" disabled="isBtnLoading" @click="onDeviceSubmit">确认</el-button>
              <el-button @click="close">取消</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: "groupEdit",
  computed: {},
  created() { },
  data() {
    let checkId = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('编号不能为空'));
      }
      if (value.length !== 20) {
        return callback(new Error('编号必须由20位数字组成'));
      }
      let catalogType = value.substring(10, 13);
      // 216 为虚拟组织 215 为业务分组；目录第一级必须为业务分组， 业务分组下为虚拟组织，虚拟组织下可以有其他虚拟组织
      if (this.level === 1 && catalogType !== "215") {
        return callback(new Error('业务分组模式下第一层目录的编号11到13位必须为215'));
      }
      if (this.level > 1 && catalogType !== "216") {
        return callback(new Error('业务分组模式下第一层以下目录的编号11到13位必须为216'));
      }
      callback();
    }
    return {
      submitCallback: null,
      showDialog: false,
      isLoging: false,
      level: 0,
      otype: -1,// otype 1-新增节点，2-编辑节点，3-导入设备
      form: {
        catalogId: null,
        name: null,
        parentId: null,
      },
      rules: {
        name: [{ required: true, message: "请输入名称", trigger: "blur" }],
        catalogId: [{ required: true, trigger: "blur", validator: checkId }]
      },
      dform: {
        parentId: null,
        parentName: null,
        deviceId: null,
        deviceName: null,
        catalogId: null,
        name: null
      },
      drules: {
        name: [{ required: true, message: "请输入名称", trigger: "blur" }],
        catalogId: [{ required: true, trigger: "blur", validator: checkId }]
      },

      deviceList: [],
      isBtnLoading: false
    };
  },
  methods: {
    openDialog: function (otype, data, callback) {
      this.otype = otype
      console.log("parentId: ", this.otype, data)
      if (otype == 1 || otype == 2) {
        this.form.id = data.id;
        this.form.catalogId = data.catalogId;
        this.form.name = data.name;
        this.form.parentId = data.parentId;
        this.showDialog = true;
        this.submitCallback = callback;
        this.level = data.level;
      } else if (otype == 3) {
        this.getDeviceList()
        Object.assign(this.dform, {
          parentId: data.parentId,
          parentName: data.parentName,
          deviceId: null,
          deviceName: null,
          catalogId: null,
          name: null,
        })
        this.showDialog = true;
        this.submitCallback = callback;
        this.level = data.level;
      }
    },
    onSubmit: function () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.isBtnLoading = true
          this.$axios({
            method: "post",
            url: `/api/device/query/channelCatalog/${this.otype == 2 ? 'updateChannelCatalogName' : 'addChannelCatalog'}/`,
            params: this.form
          }).then((res) => {
            if (res.data.code === 0) {
              if (this.submitCallback) this.submitCallback(this.form)
            } else {
              this.$message({ showClose: true, message: res.data.msg, type: "error", });
            }
            this.isBtnLoading = false
            this.close();
          }).catch((error) => { console.log(error); this.isBtnLoading = false });
        }
      })

    },
    changeDevice(e) {
      this.dform.deviceId = e
    },
    getDeviceList: function () {
      this.$axios({
        method: 'get',
        url: `/api/device/query/devices`,
        params: { page: 1, count: 100000 }
      }).then((res) => {
        if (res.data.code === 0) {
          this.deviceList = res.data.data.list;
        }
      }).catch((error) => {
        console.error(error);
      });
    },
    onDeviceSubmit: function () {
      this.$refs.dform.validate((valid) => {
        this.$axios({
          method: "post",
          url: `/api/device/query/channelCatalog/importChannelCatalog/`,
          params: this.dform
        }).then((res) => {
          if (res.data.code === 0) {
            if (this.submitCallback) this.submitCallback(this.dform)
          } else {
            this.$message({ showClose: true, message: res.data.msg, type: "error", });
          }
          this.close();
        }).catch((error) => { console.log(error); });
      })
    },
    close: function () {
      this.otype = -1;

      this.form.id = null;
      this.form.name = null;
      this.form.parentId = null;

      this.callback = null;
      this.showDialog = false;

    },
  },
};
</script>
