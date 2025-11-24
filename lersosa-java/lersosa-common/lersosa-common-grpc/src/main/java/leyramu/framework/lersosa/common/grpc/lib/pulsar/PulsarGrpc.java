package leyramu.framework.lersosa.common.grpc.lib.pulsar;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.68.0)",
    comments = "Source: Pulsar.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PulsarGrpc {

  private PulsarGrpc() {}

  public static final java.lang.String SERVICE_NAME = "Pulsar";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList,
      leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarMatcherMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PulsarMatcher",
      requestType = leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList.class,
      responseType = leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList,
      leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarMatcherMethod() {
    io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList, leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarMatcherMethod;
    if ((getPulsarMatcherMethod = PulsarGrpc.getPulsarMatcherMethod) == null) {
      synchronized (PulsarGrpc.class) {
        if ((getPulsarMatcherMethod = PulsarGrpc.getPulsarMatcherMethod) == null) {
          PulsarGrpc.getPulsarMatcherMethod = getPulsarMatcherMethod =
              io.grpc.MethodDescriptor.<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList, leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PulsarMatcher"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply.getDefaultInstance()))
              .setSchemaDescriptor(new PulsarMethodDescriptorSupplier("PulsarMatcher"))
              .build();
        }
      }
    }
    return getPulsarMatcherMethod;
  }

  private static volatile io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList,
      leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarScoresMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "PulsarScores",
      requestType = leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList.class,
      responseType = leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList,
      leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarScoresMethod() {
    io.grpc.MethodDescriptor<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList, leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> getPulsarScoresMethod;
    if ((getPulsarScoresMethod = PulsarGrpc.getPulsarScoresMethod) == null) {
      synchronized (PulsarGrpc.class) {
        if ((getPulsarScoresMethod = PulsarGrpc.getPulsarScoresMethod) == null) {
          PulsarGrpc.getPulsarScoresMethod = getPulsarScoresMethod =
              io.grpc.MethodDescriptor.<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList, leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "PulsarScores"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply.getDefaultInstance()))
              .setSchemaDescriptor(new PulsarMethodDescriptorSupplier("PulsarScores"))
              .build();
        }
      }
    }
    return getPulsarScoresMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PulsarStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PulsarStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PulsarStub>() {
        @java.lang.Override
        public PulsarStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PulsarStub(channel, callOptions);
        }
      };
    return PulsarStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PulsarBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PulsarBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PulsarBlockingStub>() {
        @java.lang.Override
        public PulsarBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PulsarBlockingStub(channel, callOptions);
        }
      };
    return PulsarBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PulsarFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PulsarFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PulsarFutureStub>() {
        @java.lang.Override
        public PulsarFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PulsarFutureStub(channel, callOptions);
        }
      };
    return PulsarFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void pulsarMatcher(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList request,
        io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPulsarMatcherMethod(), responseObserver);
    }

    /**
     */
    default void pulsarScores(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList request,
        io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPulsarScoresMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Pulsar.
   */
  public static abstract class PulsarImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PulsarGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Pulsar.
   */
  public static final class PulsarStub
      extends io.grpc.stub.AbstractAsyncStub<PulsarStub> {
    private PulsarStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PulsarStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PulsarStub(channel, callOptions);
    }

    /**
     */
    public void pulsarMatcher(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList request,
        io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPulsarMatcherMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pulsarScores(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList request,
        io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPulsarScoresMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Pulsar.
   */
  public static final class PulsarBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PulsarBlockingStub> {
    private PulsarBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PulsarBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PulsarBlockingStub(channel, callOptions);
    }

    /**
     */
    public leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply pulsarMatcher(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPulsarMatcherMethod(), getCallOptions(), request);
    }

    /**
     */
    public leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply pulsarScores(leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getPulsarScoresMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Pulsar.
   */
  public static final class PulsarFutureStub
      extends io.grpc.stub.AbstractFutureStub<PulsarFutureStub> {
    private PulsarFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PulsarFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PulsarFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> pulsarMatcher(
        leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPulsarMatcherMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply> pulsarScores(
        leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPulsarScoresMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PULSAR_MATCHER = 0;
  private static final int METHODID_PULSAR_SCORES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PULSAR_MATCHER:
          serviceImpl.pulsarMatcher((leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList) request,
              (io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>) responseObserver);
          break;
        case METHODID_PULSAR_SCORES:
          serviceImpl.pulsarScores((leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList) request,
              (io.grpc.stub.StreamObserver<leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getPulsarMatcherMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarMatcherRequestList,
              leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>(
                service, METHODID_PULSAR_MATCHER)))
        .addMethod(
          getPulsarScoresMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarScoresRequestList,
              leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.PulsarReply>(
                service, METHODID_PULSAR_SCORES)))
        .build();
  }

  private static abstract class PulsarBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PulsarBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return leyramu.framework.lersosa.common.grpc.lib.pulsar.PulsarOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Pulsar");
    }
  }

  private static final class PulsarFileDescriptorSupplier
      extends PulsarBaseDescriptorSupplier {
    PulsarFileDescriptorSupplier() {}
  }

  private static final class PulsarMethodDescriptorSupplier
      extends PulsarBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PulsarMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PulsarGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PulsarFileDescriptorSupplier())
              .addMethod(getPulsarMatcherMethod())
              .addMethod(getPulsarScoresMethod())
              .build();
        }
      }
    }
    return result;
  }
}
